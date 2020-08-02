import * as BoardHelper from '../../impl/game/board-helper';
import * as StateApiHelper from '../../impl/game/state-api-helper';
import { GameResponse, createGameResponse } from '../../model/response/game-response';
import type { BoardRequest } from '../../model/request/board-request';
import type { RouteHandler } from '../../model/core';

export const create: RouteHandler<BoardRequest, GameResponse> = async context => {
  const board = await BoardHelper.createBoard(context.request, context.user);
  const state = await StateApiHelper.createState(board);
  const stateResponse = await StateApiHelper.createStateResponse(board, state, context.user);
  return createGameResponse(board, stateResponse);
};
/*
  @GET
  @Path(PATH_GAME_ID)
  public GameResponse get(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId)
      throws CatanException {
      Board board = board(gameId);
      State state = stateApiHelper.getState(gameId, null);
      StateResponse stateResponse = stateApiHelper.createStateResponse(board, state, user.getId());
      return new GameResponse(board, stateResponse);
  }

  @GET
  @Path(PATH_BOARD)
  public Board board(@PathParam(PARAM_GAME_ID) String gameId) throws CatanException {
      return boardHelper.getBoard(gameId);
  }

  @GET
  @Path(PATH_STATE)
  @ETagHeaderSupport
  public StateResponse state(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
      @HeaderParam(HEADER_IF_NONE_MATCH) String etag) throws CatanException {
      State state = stateApiHelper.getState(gameId, etag);
      return stateApiHelper.createStateResponse(null, state, user.getId());
  }
*/
