import type { IdentifiableEntity, StrongEntity } from '../core';
import type { Board } from '../game/board';
import type { StateResponse } from './state-response';
import type { Writable } from 'ts-essentials';

export interface GameResponse extends IdentifiableEntity, StrongEntity {
  board: BoardResponse;
  state: StateResponse;
}

export type BoardResponse = Board;

export function createGameResponse(board: BoardResponse, state: StateResponse): GameResponse {
  const etag = state.etag;
  delete (state as Writable<StateResponse>).etag;
  return { id: board.id, board, state, etag };
}
