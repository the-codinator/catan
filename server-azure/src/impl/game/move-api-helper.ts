import type { BodyLessRequest, MoveRequest } from '../../model/request';
import { StateResponse, createStateResponse } from '../../model/response/state-response';
import { UserGame, createUserGame, createUserGamesFromBoard } from '../../model/game/user-game';
import { checkPlayerTurn, ensurePhaseForMove } from './game-utility';
import type { Board } from '../../model/game/board';
import type { CatanContext } from '../../core/catan-context';
import { CatanError } from '../../core/catan-error';
import type { Color } from '../../model/game/color';
import type { DeepReadonly } from 'ts-essentials';
import type { OutOfTurnApi } from '../../model/game/out-of-turn-api';
import { PRECONDITION_FAILED } from 'http-status-codes';
import { Phase } from '../../model/game/phase';
import type { State } from '../../model/game/state';
import type { StrongEntity } from '../../model/core';
import type { Writable } from 'ts-essentials';
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
  context: Pick<CatanContext<T>, 'user' | 'gameId' | 'etag' | 'request'>,
  handler: (options: PlayOptions<T>) => void,
  ...validPhases: Phase[]
): Promise<StateResponse> {
  const { user, gameId, etag, request } = context;
  const board = await getBoard(gameId);
  const state = await getState(gameId, undefined);
  const current = state.currentMove.color;
  if (etag && etag !== state.etag) {
    throw new CatanError('Someone made a move before you. Please retry with updated game state.', PRECONDITION_FAILED);
  }
  const color = checkPlayerTurn(board, state, user, outOfTurnApi);
  ensurePhaseForMove(state, validPhases);
  const options: PlayOptions<T> = { board, state, color, request };
  handler(options); // Mutates state
  try {
    await dataConnector.updateState(state);
    const updated = state.currentMove.color;
    if (updated !== current) {
      const oldUG = createUserGame(board, current);
      const newUG = createUserGame(board, updated);
      (newUG as Writable<UserGame>).myTurn = true;
      await dataConnector.updateUserGames(oldUG, newUG);
    } else if (state.phase === Phase.end && board.completed === 0) {
      board.completed = Date.now();
      await dataConnector.updateBoard(board);
      const ugs = createUserGamesFromBoard(board);
      await dataConnector.updateUserGames(...ugs);
    }
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
