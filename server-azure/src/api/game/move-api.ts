import * as SetupMoveHelper from '../../impl/game/setup-move-helper';
import type { MoveRequest } from '../../model/request';
import { Phase } from '../../model/game/phase';
import type { RouteHandler } from '../../model/core';
import type { SetupMoveRequest } from '../../model/request/game-request';
import type { StateResponse } from '../../model/response/state-response';
import { play } from '../../impl/game/move-api-helper';

type Move<T extends MoveRequest> = RouteHandler<T, StateResponse>;

export const setup: Move<SetupMoveRequest> = context =>
  play(
    undefined,
    context.user,
    context.gameId,
    context.etag,
    context.request,
    SetupMoveHelper.play,
    Phase.setup1,
    Phase.setup2
  );

/*
public class MoveApi {

    private final MoveApiHelper moveApiHelper;
    private final SetupMoveHelper setupMoveHelper;
    private final BuildMoveHelper buildMoveHelper;
    private final ThiefMoveHelper thiefMoveHelper;
    private final MiscMoveHelper miscMoveHelper;

    @POST
    @Path(PATH_ROLL)
    public StateResponse roll(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag) throws CatanException {
        return moveApiHelper.play(user.getId(), gameId, etag, miscMoveHelper::roll, Phase.gameplay);
    }

    @POST
    @Path(PATH_BUILD_ROAD)
    public StateResponse road(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag, RoadRequest request) throws CatanException {
        return moveApiHelper.play(user.getId(), gameId, etag, request, buildMoveHelper::road, Phase.gameplay);
    }

    @POST
    @Path(PATH_BUILD_HOUSE)
    public StateResponse house(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag, HouseRequest request) throws CatanException {
        return moveApiHelper.play(user.getId(), gameId, etag, request, buildMoveHelper::house, Phase.gameplay);
    }

    @POST
    @Path(PATH_THIEF_DROP)
    public StateResponse thiefDrop(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag, ThiefDropRequest request) throws CatanException {
        return moveApiHelper.play(OutOfTurnApi.THIEF, user.getId(), gameId, etag, request, thiefMoveHelper::thiefDrop,
            Phase.thief);
    }

    @POST
    @Path(PATH_THIEF_PLAY)
    public StateResponse thiefPlay(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag, ThiefPlayRequest request) throws CatanException {
        return moveApiHelper.play(user.getId(), gameId, etag, request, thiefMoveHelper::thiefPlay, Phase.thief);
    }

    @POST
    @Path(PATH_END)
    public StateResponse end(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag) throws CatanException {
        return moveApiHelper.play(user.getId(), gameId, etag, miscMoveHelper::endTurn, Phase.gameplay);
    }
}
*/
