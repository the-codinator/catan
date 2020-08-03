import * as GameUtility from './game-utility';
import { INTERNAL_SERVER_ERROR, NOT_FOUND } from 'http-status-codes';
import { State, createState as createNewState } from '../../model/game/state';
import { StateResponse, createStateResponse as createNewStateResponse } from '../../model/response/state-response';
import type { Board } from '../../model/game/board';
import { CatanError } from '../../core/catan-error';
import dataConnector from '../data/catan-data-connector';

export async function createState(board: Board): Promise<State> {
  const state = createNewState(board.id, board.players[0].color, GameUtility.findDesert(board));
  try {
    await dataConnector.createState(state);
  } catch (e) {
    throw new CatanError('Error creating state', INTERNAL_SERVER_ERROR, e);
  }
  return state;
}

export async function createStateResponse(
  board: Board | undefined,
  state: State,
  userId: string
): Promise<StateResponse> {
  if (!board) {
    try {
      board = await dataConnector.getBoard(state.id);
    } catch (e) {
      if (e.errorStatus === NOT_FOUND) {
        throw new CatanError('Could not find board with id - ' + state.id, NOT_FOUND, e);
      } else {
        throw e;
      }
    }
  }
  for (const player of board.players) {
    if (player.id === userId) {
      return createNewStateResponse(state, player.color);
    }
  }
  return createNewStateResponse(state, undefined);
}

/**
 * Get state with id {@param gameId} and not matching {@param etag}
 */
export async function getState(gameId: string, etag: undefined): Promise<State>;
export async function getState(gameId: string, etag: string | undefined): Promise<State | undefined>;
export async function getState(gameId: string, etag: string | undefined): Promise<State | undefined> {
  try {
    return await dataConnector.getState(gameId, etag);
  } catch (e) {
    if (e.errorStatus === NOT_FOUND) {
      throw new CatanError('Could not find state with id - ' + gameId, NOT_FOUND, e);
    } else {
      throw e;
    }
  }
}
