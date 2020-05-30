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
import static org.codi.catan.util.Constants.PATH_BUILD_CITY;
import static org.codi.catan.util.Constants.PATH_BUILD_ROAD;
import static org.codi.catan.util.Constants.PATH_BUILD_SETTLEMENT;
import static org.codi.catan.util.Constants.PATH_DEV_BUY;
import static org.codi.catan.util.Constants.PATH_DEV_PLAY;
import static org.codi.catan.util.Constants.PATH_END;
import static org.codi.catan.util.Constants.PATH_ROLL;
import static org.codi.catan.util.Constants.PATH_SETUP;
import static org.codi.catan.util.Constants.PATH_THIEF;

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
import org.codi.catan.impl.game.DevCardMoveHelper;
import org.codi.catan.impl.game.MiscMoveHelper;
import org.codi.catan.impl.game.MoveApiHelper;
import org.codi.catan.impl.game.SetupMoveHelper;
import org.codi.catan.impl.game.StateApiHelper;
import org.codi.catan.model.game.Phase;
import org.codi.catan.model.request.SetupMoveRequest;
import org.codi.catan.model.response.StateResponse;
import org.codi.catan.model.user.User;

@Api(value = API_MOVE, authorizations = @Authorization(BEARER_AUTHORIZATION_KEY))
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Path(BASE_PATH_MOVE)
@ETagHeaderSupport
public class MoveApi { // TODO:

    private MoveApiHelper moveApiHelper;
    private StateApiHelper stateApiHelper;
    private SetupMoveHelper setupMoveHelper;
    private BuildMoveHelper buildMoveHelper;
    private DevCardMoveHelper devCardMoveHelper;
    private MiscMoveHelper miscMoveHelper;

    @Inject
    public MoveApi(MoveApiHelper moveApiHelper, StateApiHelper stateApiHelper, SetupMoveHelper setupMoveHelper,
        BuildMoveHelper buildMoveHelper, DevCardMoveHelper devCardMoveHelper, MiscMoveHelper miscMoveHelper) {
        this.moveApiHelper = moveApiHelper;
        this.stateApiHelper = stateApiHelper;
        this.setupMoveHelper = setupMoveHelper;
        this.buildMoveHelper = buildMoveHelper;
        this.devCardMoveHelper = devCardMoveHelper;
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
        @HeaderParam(HEADER_IF_MATCH) String etag) {
        return null;
    }

    @POST
    @Path(PATH_BUILD_ROAD)
    public StateResponse road(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag) {
        return null;
    }

    @POST
    @Path(PATH_BUILD_SETTLEMENT)
    public StateResponse house(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag) {
        return null;
    }

    @POST
    @Path(PATH_BUILD_CITY)
    public StateResponse city(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag) {
        return null;
    }

    @POST
    @Path(PATH_DEV_BUY)
    public StateResponse devBuy(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag) {
        return null;
    }

    @POST
    @Path(PATH_DEV_PLAY)
    public StateResponse devPlay(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag) {
        return null;
    }

    @POST
    @Path(PATH_THIEF)
    public StateResponse thief(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag) {
        return null;
    }

    @POST
    @Path(PATH_END)
    public StateResponse end(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag) {
        // Note: for this guy, first create the StateResponse, and then update current move
        return null;
    }
}
