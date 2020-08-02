import * as GraphHelper from './graph-helper';
import { BadRequestError, CatanError } from '../../core/catan-error';
import { Board, createBoard as createNewBoard } from '../../model/game/board';
import { DICE_COUNT, MAX_ROLL_PER_DIE, MIN_ROLL_PER_DIE } from '../../util/constants';
import { INTERNAL_SERVER_ERROR, NOT_FOUND } from 'http-status-codes';
import { RESOURCES, getTileCount } from '../../model/game/resource';
import { arrayEquals, generateRandomUuid } from '../../util/util';
import type { BoardRequest } from '../../model/request/board-request';
import { Color } from '../../model/game/color';
import type { Player } from '../../model/game/player';
import type { Ports } from '../../model/game/ports';
import type { Tile } from '../../model/game/tile';
import type { Writable } from 'ts-essentials';
import dataConnector from '../data/catan-data-connector';
import { validateUserId } from '../user/user-api-helper';

export async function createBoard(request: BoardRequest, author: string): Promise<Board> {
  const board: Board = await normalizeAndValidateBoard(request);
  if (!board.players.find(player => player.id === author)) {
    throw new BadRequestError('Board creator is not part of game');
  }
  try {
    await dataConnector.createBoard(board);
  } catch (e) {
    throw new CatanError('Error creating game', INTERNAL_SERVER_ERROR, e);
  }
  return board;
}

export async function getBoard(gameId: string): Promise<Board> {
  try {
    return await dataConnector.getBoard(gameId);
  } catch (e) {
    if (e.errorStatus === NOT_FOUND) {
      throw new CatanError('Could not find board with id - ' + gameId, NOT_FOUND, e);
    } else {
      throw e;
    }
  }
}

async function normalizeAndValidateBoard(request: BoardRequest): Promise<Board> {
  const board = createNewBoard(generateRandomUuid(), request);
  normalizeAndValidateTiles(board.tiles);
  normalizeAndValidatePorts(board.ports);
  await validatePlayers(board.players);
  return board;
}

function normalizeAndValidateTiles(tiles: Tile[]): void {
  const diceRollCounts = new Array<number>(DICE_COUNT * MAX_ROLL_PER_DIE + 1).fill(0);
  for (const resource of RESOURCES) {
    let count = 0;
    for (const tile of tiles) {
      if (tile.resource === resource) {
        count++;
        if (tile.roll < DICE_COUNT * MIN_ROLL_PER_DIE || tile.roll > DICE_COUNT * MAX_ROLL_PER_DIE) {
          // Valid dice rolls
          throw new BadRequestError('Invalid dice roll value for tile');
        }
        diceRollCounts[tile.roll]++;
      }
    }
    if (count !== getTileCount(resource)) {
      throw new BadRequestError('Invalid number of tiles for ' + resource.toString());
    }
  }
  let count = 0;
  for (const tile of tiles) {
    if (tile.resource === null) {
      count++;
      (tile as Writable<Tile>).roll = 0;
      diceRollCounts[0]++;
    }
  }
  if (count !== 1) {
    // Desert
    throw new BadRequestError('Invalid number of desert tiles');
  }
  if (!arrayEquals(GraphHelper.getDiceRollCount(), diceRollCounts)) {
    throw new BadRequestError('Invalid dice roll distribution on tiles');
  }
}

function normalizeAndValidatePorts(ports: Ports): void {
  const ports21 = ports.ports21;
  const ports31 = ports.ports31;
  if (!arrayEquals(Object.keys(ports21).sort(), RESOURCES)) {
    throw new BadRequestError('Incorrect 2:1 resource port definition');
  }
  if (ports31.length !== GraphHelper.getPortCount() - RESOURCES.length) {
    throw new BadRequestError('Incorrect 3:1 port definition');
  }
  const normalizedPorts31: number[] = [];
  for (const vertex of ports31) {
    normalizedPorts31.push(GraphHelper.normalizeAndValidatePort(vertex));
  }
  (ports as Writable<Ports>).ports31 = normalizedPorts31.sort();
  for (const resource of RESOURCES) {
    const vertex = ports21[resource];
    const normalizedVertex = GraphHelper.normalizeAndValidatePort(vertex);
    if (normalizedPorts31.includes(normalizedVertex)) {
      throw new BadRequestError('Duplicate port vertex in 2:1 and 3:1');
    }
    if (vertex !== normalizedVertex) {
      (ports21 as Writable<typeof ports21>)[resource] = normalizedVertex;
    }
  }
}

// Simplified without cache for serverless
async function validatePlayers(players: Player[]): Promise<void> {
  const colors = new Set<Color>();
  const users = new Set<string>();
  for (const player of players) {
    if (colors.has(player.color)) {
      throw new BadRequestError('Duplicate player color');
    }
    colors.add(player.color);
    validateUserId(player.id);
    if (users.has(player.id)) {
      throw new BadRequestError('Duplicate user');
    }
    users.add(player.id);
  }
  if (!arrayEquals(Array.from(colors).sort(), Object.values(Color).sort())) {
    throw new BadRequestError('Missing player color');
  }
  const usersArray = Array.from(users);
  const dbUsers = await dataConnector.getUsers(usersArray);
  if (dbUsers.length !== usersArray.length) {
    throw new BadRequestError('Invalid Users - ' + usersArray);
  }
}
