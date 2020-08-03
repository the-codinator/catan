import * as BoardHelper from '../../impl/game/board-helper';
import * as StateApiHelper from '../../impl/game/state-api-helper';
import type { AuthenticatedGetGameETagRequest, AuthenticatedGetGameRequest } from '../../model/request';
import { BoardResponse, GameResponse, createGameResponse } from '../../model/response/game-response';
import type { BoardRequest } from '../../model/request/board-request';
import type { RouteHandler } from '../../model/core';
import type { StateResponse } from '../../model/response/state-response';

export const create: RouteHandler<BoardRequest, GameResponse> = async context => {
  const board = await BoardHelper.createBoard(context.request, context.user);
  const boardResponse = BoardHelper.createBoardResponse(board);
  const state = await StateApiHelper.createState(board);
  const stateResponse = await StateApiHelper.createStateResponse(board, state, context.user);
  return createGameResponse(boardResponse, stateResponse);
};

export const get: RouteHandler<AuthenticatedGetGameRequest, GameResponse> = async context => {
  const board = await BoardHelper.getBoard(context.gameId);
  const boardResponse = BoardHelper.createBoardResponse(board);
  const state = await StateApiHelper.getState(context.gameId, undefined);
  const stateResponse = await StateApiHelper.createStateResponse(board, state, context.user);
  return createGameResponse(boardResponse, stateResponse);
};

export const board: RouteHandler<AuthenticatedGetGameRequest, BoardResponse> = async context =>
  BoardHelper.createBoardResponse(await BoardHelper.getBoard(context.gameId));

export const state: RouteHandler<AuthenticatedGetGameETagRequest, StateResponse | ''> = async context => {
  const state = await StateApiHelper.getState(context.gameId, context.etag);
  return state ? StateApiHelper.createStateResponse(undefined, state, context.user) : '';
};
