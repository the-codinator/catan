/*
 * @author the-codinator
 * created on 2020/5/24
 */

package org.codi.catan.api.game;

import static org.codi.catan.util.Constants.API_BOARD;
import static org.codi.catan.util.Constants.BASE_PATH_GAME;
import static org.codi.catan.util.Constants.BEARER_AUTHORIZATION_KEY;
import static org.codi.catan.util.Constants.PATH_GAME_ID;
import static org.codi.catan.util.Constants.PATH_LAYOUT;
import static org.codi.catan.util.Constants.PATH_STATE;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(value = API_BOARD, authorizations = @Authorization(BEARER_AUTHORIZATION_KEY))
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Path(BASE_PATH_GAME)
public class BoardApi {

    @POST
    public void create() {
    }

    @GET
    @Path(PATH_GAME_ID)
    public void get() {
        layout();
        state();
    }

    @GET
    @Path(PATH_LAYOUT)
    public void layout() {
    }

    @GET
    @Path(PATH_STATE)
    public void state() {
    }
}
