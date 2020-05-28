/*
 * @author the-codinator
 * created on 2020/5/24
 */

package org.codi.catan.api.game;

import static org.codi.catan.util.Constants.API_MOVE;
import static org.codi.catan.util.Constants.BASE_PATH_MOVE;
import static org.codi.catan.util.Constants.BEARER_AUTHORIZATION_KEY;
import static org.codi.catan.util.Constants.PATH_BUILD_CITY;
import static org.codi.catan.util.Constants.PATH_BUILD_ROAD;
import static org.codi.catan.util.Constants.PATH_BUILD_SETTLEMENT;
import static org.codi.catan.util.Constants.PATH_DEV_BUY;
import static org.codi.catan.util.Constants.PATH_DEV_PLAY;
import static org.codi.catan.util.Constants.PATH_END;
import static org.codi.catan.util.Constants.PATH_ROLL;
import static org.codi.catan.util.Constants.PATH_SETUP;
import static org.codi.catan.util.Constants.PATH_THIEF;

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
@Path(BASE_PATH_MOVE)
public class MoveApi { // TODO:

    @POST
    @Path(PATH_SETUP)
    public void setup() {
    }

    @POST
    @Path(PATH_ROLL)
    public void roll() {
    }

    @POST
    @Path(PATH_BUILD_ROAD)
    public void road() {
    }

    @POST
    @Path(PATH_BUILD_SETTLEMENT)
    public void house() {
    }

    @POST
    @Path(PATH_BUILD_CITY)
    public void city() {
    }

    @POST
    @Path(PATH_DEV_BUY)
    public void devBuy() {
    }

    @POST
    @Path(PATH_DEV_PLAY)
    public void devPlay() {
    }

    @POST
    @Path(PATH_THIEF)
    public void thief() {
    }

    @POST
    @Path(PATH_END)
    public void end() {
    }
}
