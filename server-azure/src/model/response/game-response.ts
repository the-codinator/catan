import type { IdentifiableEntity, StrongEntity } from '../core';
import type { Board } from '../game/board';
import type { StateResponse } from './state-response';

export interface GameResponse extends IdentifiableEntity, StrongEntity {
  board: Board;
  state: StateResponse;
}

export function createGameResponse(board: Board, state: StateResponse): GameResponse {
  return { id: board.id, board, state, etag: state.etag };
}
