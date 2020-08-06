import type { BodyLessRequest, MoveRequest } from '../../model/request';
import { StateResponse, createStateResponse } from '../../model/response/state-response';
import { checkPlayerTurn, ensurePhaseForMove } from './game-utility';
import type { Board } from '../../model/game/board';
import type { CatanContext } from '../../core/catan-context';
import { CatanError } from '../../core/catan-error';
import type { Color } from '../../model/game/color';
import type { DeepReadonly } from 'ts-essentials';
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
  request: DeepReadonly<T extends BodyLessRequest ? undefined : T>;
}

export async function play<T extends MoveRequest>(
  outOfTurnApi: OutOfTurnApi | undefined,
  context: CatanContext<T>,
  handler: (options: PlayOptions<T>) => void,
  ...validPhases: Phase[]
): Promise<StateResponse> {
  const { user, gameId, etag, request } = context;
  const board = await getBoard(gameId);
  const state = await getState(gameId, undefined);
  if (etag && etag !== state.etag) {
    throw new CatanError('Someone made a move before you. Please retry with updated game state.', PRECONDITION_FAILED);
  }
  const color = checkPlayerTurn(board, state, user, outOfTurnApi);
  ensurePhaseForMove(state, validPhases);
  const options: PlayOptions<T> = { board, state, color, request };
  handler(options); // Mutates state
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
