/*
 * @author the-codinator
 * created on 2020/5/24
 */

package org.codi.catan.api.game;

import static org.codi.catan.util.Constants.API_MOVE;
import static org.codi.catan.util.Constants.BASE_PATH_TRADE;
import static org.codi.catan.util.Constants.BEARER_AUTHORIZATION_KEY;
import static org.codi.catan.util.Constants.PATH_TRADE_ACCEPT;
import static org.codi.catan.util.Constants.PATH_TRADE_GAME;
import static org.codi.catan.util.Constants.PATH_TRADE_PLAYER;
import static org.codi.catan.util.Constants.PATH_TRADE_REJECT;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(value = API_MOVE, authorizations = @Authorization(BEARER_AUTHORIZATION_KEY))
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Path(BASE_PATH_TRADE)
public class TradeApi {

    @POST
    @Path(PATH_TRADE_PLAYER)
    public void player() {
    }

    @POST
    @Path(PATH_TRADE_ACCEPT)
    public void accept() {
    }

    @POST
    @Path(PATH_TRADE_REJECT)
    public void reject() {
    }

    @POST
    @Path(PATH_TRADE_GAME)
    public void game() { // input = resource, count (2,3,4)
    }
}
