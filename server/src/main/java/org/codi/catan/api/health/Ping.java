/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.api.health;

import static org.codi.catan.util.Constants.API_PING;
import static org.codi.catan.util.Constants.PATH_PING;

import io.swagger.annotations.Api;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(API_PING)
@Path(PATH_PING)
@Produces(MediaType.TEXT_PLAIN)
public class Ping {

    @GET
    public String ping() {
        return "pong";
    }
}
