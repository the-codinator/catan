/*
 * @author the-codinator
 * created on 2020/5/23
 */

package org.codi.catan.api.health;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
public class Health {

    // TODO: Proxy response from http://localhost:8081/healthcheck
    @GET
    public String health() {
        return "{\"status\": \"ok\"}";
    }
}
