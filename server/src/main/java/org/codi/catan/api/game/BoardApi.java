/*
 * @author the-codinator
 * created on 2020/5/24
 */

package org.codi.catan.api.game;

import static org.codi.catan.util.Constants.API_BOARD;
import static org.codi.catan.util.Constants.BASE_PATH_GAME;
import static org.codi.catan.util.Constants.BEARER_AUTHORIZATION_KEY;
import static org.codi.catan.util.Constants.HEADER_IF_NONE_MATCH;
import static org.codi.catan.util.Constants.PARAM_GAME_ID;
import static org.codi.catan.util.Constants.PATH_BOARD;
import static org.codi.catan.util.Constants.PATH_GAME_ID;
import static org.codi.catan.util.Constants.PATH_STATE;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.codi.catan.core.CatanException;
import org.codi.catan.filter.ResponseETagFilter.ResponseFilterAnnotation;
import org.codi.catan.impl.game.LayoutHelper;
import org.codi.catan.impl.game.StateHelper;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.State;
import org.codi.catan.model.response.GameResponse;
import org.codi.catan.model.response.StateResponse;
import org.codi.catan.model.user.User;

@Api(value = API_BOARD, authorizations = @Authorization(BEARER_AUTHORIZATION_KEY))
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Path(BASE_PATH_GAME)
public class BoardApi {

    private final LayoutHelper layoutHelper;
    private final StateHelper stateHelper;

    @Inject
    public BoardApi(LayoutHelper layoutHelper, StateHelper stateHelper) {
        this.layoutHelper = layoutHelper;
        this.stateHelper = stateHelper;
    }

    @POST
    public GameResponse create(@ApiParam(hidden = true) @Auth User user, Board board) throws CatanException {
        board = layoutHelper.create(board, user.getId());
        State state = stateHelper.createState(board);
        return new GameResponse(board, stateHelper.createStateResponse(state, board, user.getId()));
    }

    @GET
    @Path(PATH_GAME_ID)
    public GameResponse get(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId)
        throws CatanException {
        Board board = board(gameId);
        StateResponse state = state(user, gameId, null);
        return new GameResponse(board, state);
    }

    @GET
    @Path(PATH_BOARD)
    public Board board(@PathParam(PARAM_GAME_ID) String gameId) throws CatanException {
        return layoutHelper.getBoard(gameId);
    }

    @GET
    @Path(PATH_STATE)
    @ResponseFilterAnnotation
    public StateResponse state(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_NONE_MATCH) String etag) throws CatanException {
        return stateHelper.createStateResponse(stateHelper.getState(gameId, etag), null, user.getId());
    }
}
