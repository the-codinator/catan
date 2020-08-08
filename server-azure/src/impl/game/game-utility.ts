import { BadRequestError, CatanError } from '../../core/catan-error';
import { MAX_ROLL_PER_DIE, MIN_ROLL_PER_DIE } from '../../util/constants';
import { RESOURCES, Resource } from '../../model/game/resource';

import { Board } from '../../model/game/board';
import { Color } from '../../model/game/color';
import type { DeepReadonly } from 'ts-essentials';
import { FORBIDDEN } from 'http-status-codes';
import { OutOfTurnApi } from '../../model/game/out-of-turn-api';
import { Phase } from '../../model/game/phase';
import { State } from '../../model/game/state';
import { addToFrequencyMap } from '../../util/util';
import { getComplementaryPortVertex } from './graph-helper';
import { getResourceCount } from '../../model/game/hand';

export function checkPlayerTurn(board: Board, state: State, user: string, outOfTurnApi?: OutOfTurnApi): Color {
  if (outOfTurnApi === OutOfTurnApi.ADMIN) {
    return state.currentMove.color;
  }
  const color = board.players.find(player => player.id === user)?.color;
  if (!color) {
    throw new CatanError("Cannot make moves in a game you aren't playing!", FORBIDDEN);
  }
  switch (outOfTurnApi) {
    case undefined:
      if (color === state.currentMove.color) {
        return color;
      } else {
        throw new CatanError('Cannot play this move out of turn', FORBIDDEN);
      }
    case OutOfTurnApi.THIEF:
      if (state.currentMove.thieved?.includes(color)) {
        return color;
      }
      break;
    case OutOfTurnApi.TRADE:
      return color;
  }
  throw new CatanError('Cannot play this move out of turn', FORBIDDEN);
}

export function ensurePhaseForMove(state: State, validPhases: Phase[]): void {
  if (!validPhases.includes(state.phase)) {
    throw new CatanError(`Cannot play this move in [${state.phase}] phase`, FORBIDDEN);
  }
}

export function findDesert(board: Board): number {
  const desert = board.tiles.findIndex(tile => tile.resource === null);
  if (desert === -1) {
    throw new CatanError('Invalid Board - Desert not found');
  }
  return desert;
}

export function transferResources(
  state: State,
  from: Color | undefined,
  to: Color | undefined,
  resource: Resource | undefined,
  count: number
): number {
  // No transfer
  if (from === to || resource === undefined || count === 0) {
    return 0;
  }
  if (!Number.isInteger(count)) {
    throw new CatanError('Fraction resource transfer ?!?');
  }
  // Inverse transfer
  if (count < 0) {
    count = -count;
    [from, to] = [to, from];
  }
  // Get required resource maps
  const fromResources = from ? state.hands[from].resources : state.bank;
  const toResources = to ? state.hands[to].resources : state.bank;
  // Ensure available resources to transfer
  if (!from) {
    count = Math.min(count, fromResources[resource] ?? 0);
  } else if ((fromResources[resource] ?? 0) < count) {
    throw new BadRequestError(`Not enough [${resource}] to perform this move`);
  }
  // Perform transfer
  addToFrequencyMap(fromResources, resource, -count);
  addToFrequencyMap(toResources, resource, count);
  // Return actual transfer amount
  return count;
}

export function transferResourcesList(
  state: State,
  from: Color | undefined,
  to: Color | undefined,
  ...resources: (Resource | undefined)[]
): number {
  return resources.reduce((count, resource) => count + transferResources(state, from, to, resource, 1), 0);
}

export function transferResourcesMap(
  state: State,
  from: Color | undefined,
  to: Color | undefined,
  resources: Partial<Record<Resource, number>>
): number {
  return (Object.keys(resources) as Resource[]).reduce(
    (count, resource) => count + transferResources(state, from, to, resource, resources[resource]!),
    0
  );
}

export function rollDice(): number {
  return MIN_ROLL_PER_DIE + Math.floor(Math.random() * (MAX_ROLL_PER_DIE - MIN_ROLL_PER_DIE + 1));
}

/**
 * Get all tiles from board matching roll
 */
export function findTileHexesForRoll(board: DeepReadonly<Board>, roll: number): number[] {
  const tiles: number[] = [];
  board.tiles.forEach((tile, i) => tile.roll === roll && tiles.push(i));
  return tiles;
}

/**
 * Choose a random resource card from {@param color}'s hand to be stolen
 */
export function chooseRandomlyStolenCard(state: State, color: Color): Resource | undefined {
  const hand = state.hands[color];
  const count = getResourceCount(hand);
  if (count === 0) {
    return undefined;
  }
  let theChosenOne = Math.floor(Math.random() * count);
  const resources = hand.resources;
  for (const resource of RESOURCES) {
    theChosenOne -= resources[resource] ?? 0;
    if (theChosenOne < 0) {
      return resource;
    }
  }
  return undefined; // Will never happen
}

export function hasHouseOnPort(board: DeepReadonly<Board>, state: State, resource?: Resource | undefined): boolean {
  return (
    (resource ? [board.ports.ports21[resource]] : board.ports.ports31).findIndex(
      port => (state.houses[port] || state.houses[getComplementaryPortVertex(port)])?.color === state.currentMove.color
    ) !== -1
  );
}
