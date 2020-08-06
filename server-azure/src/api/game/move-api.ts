import * as BuildMoveHelper from '../../impl/game/build-move-helper';
import * as MiscMoveHelper from '../../impl/game/misc-move-helper';
import * as SetupMoveHelper from '../../impl/game/setup-move-helper';
import type {
  HouseRequest,
  RoadRequest,
  SetupMoveRequest,
  _BodyLessMoveRequest,
} from '../../model/request/game-request';
import type { MoveRequest } from '../../model/request';
import { Phase } from '../../model/game/phase';
import type { RouteHandler } from '../../model/core';
import type { StateResponse } from '../../model/response/state-response';
import { play } from '../../impl/game/move-api-helper';

type Move<T extends MoveRequest> = RouteHandler<T, StateResponse>;

export const setup: Move<SetupMoveRequest> = context =>
  play(undefined, context, SetupMoveHelper.play, Phase.setup1, Phase.setup2);

export const roll: Move<_BodyLessMoveRequest> = context =>
  play(undefined, context, MiscMoveHelper.roll, Phase.gameplay);

export const road: Move<RoadRequest> = context => play(undefined, context, BuildMoveHelper.road, Phase.gameplay);
export const house: Move<HouseRequest> = context => play(undefined, context, BuildMoveHelper.house, Phase.gameplay);

export const end: Move<_BodyLessMoveRequest> = context => play(undefined, context, MiscMoveHelper.end, Phase.gameplay);

/*
public class MoveApi {

    private final MoveApiHelper moveApiHelper;
    private final SetupMoveHelper setupMoveHelper;
    private final BuildMoveHelper buildMoveHelper;
    private final ThiefMoveHelper thiefMoveHelper;
    private final MiscMoveHelper miscMoveHelper;

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
