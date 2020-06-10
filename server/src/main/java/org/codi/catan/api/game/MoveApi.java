/*
 * @author the-codinator
 * created on 2020/5/24
 */

package org.codi.catan.api.game;

import static org.codi.catan.util.Constants.API_MOVE;
import static org.codi.catan.util.Constants.BASE_PATH_MOVE;
import static org.codi.catan.util.Constants.BEARER_AUTHORIZATION_KEY;
import static org.codi.catan.util.Constants.HEADER_IF_MATCH;
import static org.codi.catan.util.Constants.PARAM_GAME_ID;
import static org.codi.catan.util.Constants.PATH_BUILD_HOUSE;
import static org.codi.catan.util.Constants.PATH_BUILD_ROAD;
import static org.codi.catan.util.Constants.PATH_END;
import static org.codi.catan.util.Constants.PATH_ROLL;
import static org.codi.catan.util.Constants.PATH_SETUP;
import static org.codi.catan.util.Constants.PATH_THIEF_DROP;
import static org.codi.catan.util.Constants.PATH_THIEF_PLAY;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.codi.catan.core.CatanException;
import org.codi.catan.filter.ETagHeaderFilter.ETagHeaderSupport;
import org.codi.catan.impl.game.BuildMoveHelper;
import org.codi.catan.impl.game.MiscMoveHelper;
import org.codi.catan.impl.game.MoveApiHelper;
import org.codi.catan.impl.game.SetupMoveHelper;
import org.codi.catan.impl.game.ThiefMoveHelper;
import org.codi.catan.model.game.OutOfTurnApi;
import org.codi.catan.model.game.Phase;
import org.codi.catan.model.request.HouseRequest;
import org.codi.catan.model.request.RoadRequest;
import org.codi.catan.model.request.SetupMoveRequest;
import org.codi.catan.model.request.ThiefDropRequest;
import org.codi.catan.model.request.ThiefPlayRequest;
import org.codi.catan.model.response.StateResponse;
import org.codi.catan.model.user.User;

@Api(value = API_MOVE, authorizations = @Authorization(BEARER_AUTHORIZATION_KEY))
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Path(BASE_PATH_MOVE)
@ETagHeaderSupport
public class MoveApi {

    private final MoveApiHelper moveApiHelper;
    private final SetupMoveHelper setupMoveHelper;
    private final BuildMoveHelper buildMoveHelper;
    private final ThiefMoveHelper thiefMoveHelper;
    private final MiscMoveHelper miscMoveHelper;

    @Inject
    public MoveApi(MoveApiHelper moveApiHelper, SetupMoveHelper setupMoveHelper, BuildMoveHelper buildMoveHelper,
        ThiefMoveHelper thiefMoveHelper, MiscMoveHelper miscMoveHelper) {
        this.moveApiHelper = moveApiHelper;
        this.setupMoveHelper = setupMoveHelper;
        this.buildMoveHelper = buildMoveHelper;
        this.thiefMoveHelper = thiefMoveHelper;
        this.miscMoveHelper = miscMoveHelper;
    }

    @POST
    @Path(PATH_SETUP)
    public StateResponse setup(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag, SetupMoveRequest request) throws CatanException {
        return moveApiHelper.play(user.getId(), gameId, etag, request, setupMoveHelper::play, Phase.setup1,
            Phase.setup2);
    }

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
