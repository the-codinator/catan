import { StateResponse, createStateResponse } from '../../model/response/state-response';
import { checkPlayerTurn, ensurePhaseForMove } from './game-utility';
import type { Board } from '../../model/game/board';
import { CatanError } from '../../core/catan-error';
import type { Color } from '../../model/game/color';
import type { DeepReadonly } from 'ts-essentials';
import type { MoveRequest } from '../../model/request';
import { OutOfTurnApi } from '../../model/game/out-of-turn-api';
import { PRECONDITION_FAILED } from 'http-status-codes';
import type { Phase } from '../../model/game/phase';
import type { State } from '../../model/game/state';
import { StrongEntity } from '../../model/core';
import dataConnector from '../data/catan-data-connector';
import { getBoard } from './board-helper';
import { getState } from './state-api-helper';

export interface PlayOptions<T extends MoveRequest> {
  board: DeepReadonly<Board>;
  state: State & Readonly<StrongEntity>;
  color: Readonly<Color>;
  request: T;
}

export async function play<T extends MoveRequest>(
  outOfTurnApi: OutOfTurnApi | undefined,
  userId: string,
  gameId: string,
  etag: string | undefined,
  request: T,
  handler: (options: PlayOptions<T>) => void,
  ...validPhases: Phase[]
): Promise<StateResponse> {
  const board = await getBoard(gameId);
  const state = await getState(gameId, undefined);
  if (etag && etag !== state.etag) {
    throw new CatanError('Someone made a move before you. Please retry with updated game state.', PRECONDITION_FAILED);
  }
  const color = checkPlayerTurn(board, state, userId, outOfTurnApi);
  ensurePhaseForMove(state, validPhases);
  // if (handler.shouldValidateInput()) {
  //     Util.validateInput(request);
  // }
  handler({ board, state, color, request }); // Mutates state
  try {
    await dataConnector.updateState(state);
  } catch (e) {
    if (e.errorState === PRECONDITION_FAILED) {
      throw new CatanError(
        'Someone made a move before you. Please retry with updated game state.',
        PRECONDITION_FAILED,
        e
      );
    } else {
      throw e;
    }
  }
  return createStateResponse(state, color);
}
