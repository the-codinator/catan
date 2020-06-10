/*
 * @author the-codinator
 * created on 2020/6/10
 */

package org.codi.catan.api.game;

import static org.codi.catan.util.Constants.API_MOVE;
import static org.codi.catan.util.Constants.BASE_PATH_DEV;
import static org.codi.catan.util.Constants.BEARER_AUTHORIZATION_KEY;
import static org.codi.catan.util.Constants.HEADER_IF_MATCH;
import static org.codi.catan.util.Constants.PARAM_GAME_ID;
import static org.codi.catan.util.Constants.PATH_DEV_BUY;
import static org.codi.catan.util.Constants.PATH_DEV_PLAY;

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
import org.codi.catan.impl.game.DevCardMoveHelper;
import org.codi.catan.impl.game.MoveApiHelper;
import org.codi.catan.model.game.Phase;
import org.codi.catan.model.request.DevCardRequest;
import org.codi.catan.model.response.StateResponse;
import org.codi.catan.model.user.User;

@Api(value = API_MOVE, authorizations = @Authorization(BEARER_AUTHORIZATION_KEY))
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Path(BASE_PATH_DEV)
@ETagHeaderSupport
public class DevCardApi {

    private final MoveApiHelper moveApiHelper;
    private final DevCardMoveHelper devCardMoveHelper;

    @Inject
    public DevCardApi(MoveApiHelper moveApiHelper, DevCardMoveHelper devCardMoveHelper) {
        this.moveApiHelper = moveApiHelper;
        this.devCardMoveHelper = devCardMoveHelper;
    }

    @POST
    @Path(PATH_DEV_BUY)
    public StateResponse buy(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag) throws CatanException {
        return moveApiHelper.play(user.getId(), gameId, etag, devCardMoveHelper::buy, Phase.gameplay);
    }

    @POST
    @Path(PATH_DEV_PLAY)
    public StateResponse play(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag, DevCardRequest request) throws CatanException {
        return moveApiHelper.play(user.getId(), gameId, etag, request, devCardMoveHelper::play, Phase.gameplay);
    }
}
