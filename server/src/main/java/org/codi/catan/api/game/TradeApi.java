/*
 * @author the-codinator
 * created on 2020/5/24
 */

package org.codi.catan.api.game;

import static org.codi.catan.util.Constants.API_MOVE;
import static org.codi.catan.util.Constants.BASE_PATH_TRADE;
import static org.codi.catan.util.Constants.BEARER_AUTHORIZATION_KEY;
import static org.codi.catan.util.Constants.HEADER_IF_MATCH;
import static org.codi.catan.util.Constants.PARAM_GAME_ID;
import static org.codi.catan.util.Constants.PATH_TRADE_BANK;
import static org.codi.catan.util.Constants.PATH_TRADE_OFFER;
import static org.codi.catan.util.Constants.PATH_TRADE_RESPOND;

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
import org.codi.catan.impl.game.MoveApiHelper;
import org.codi.catan.impl.game.TradeApiHelper;
import org.codi.catan.model.game.OutOfTurnApi;
import org.codi.catan.model.game.Phase;
import org.codi.catan.model.request.TradeBankRequest;
import org.codi.catan.model.request.TradePlayerRequest;
import org.codi.catan.model.request.TradeResponseRequest;
import org.codi.catan.model.response.StateResponse;
import org.codi.catan.model.user.User;

@Api(value = API_MOVE, authorizations = @Authorization(BEARER_AUTHORIZATION_KEY))
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Path(BASE_PATH_TRADE)
public class TradeApi {

    private final MoveApiHelper moveApiHelper;
    private final TradeApiHelper tradeApiHelper;

    @Inject
    public TradeApi(MoveApiHelper moveApiHelper, TradeApiHelper tradeApiHelper) {
        this.moveApiHelper = moveApiHelper;
        this.tradeApiHelper = tradeApiHelper;
    }

    @POST
    @Path(PATH_TRADE_BANK)
    public StateResponse bank(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag, TradeBankRequest request) throws CatanException {
        return moveApiHelper.play(user.getId(), gameId, etag, request, tradeApiHelper::bank, Phase.gameplay);
    }

    @POST
    @Path(PATH_TRADE_OFFER)
    public StateResponse offer(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag, TradePlayerRequest request) throws CatanException {
        return moveApiHelper.play(OutOfTurnApi.TRADE, user.getId(), gameId, etag, request, tradeApiHelper::offer,
            Phase.gameplay);
    }

    @POST
    @Path(PATH_TRADE_RESPOND)
    public StateResponse respond(@ApiParam(hidden = true) @Auth User user, @PathParam(PARAM_GAME_ID) String gameId,
        @HeaderParam(HEADER_IF_MATCH) String etag, TradeResponseRequest request) throws CatanException {
        return moveApiHelper.play(OutOfTurnApi.TRADE, user.getId(), gameId, etag, request, tradeApiHelper::respond,
            Phase.gameplay);
    }
}
