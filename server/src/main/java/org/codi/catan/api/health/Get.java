/*
 * @author the-codinator
 * created on 2020/7/2
 */

package org.codi.catan.api.health;

import static org.codi.catan.util.Constants.PATH_ROOT;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path(PATH_ROOT)
public class Get {

    private static final Response response = Response.status(Status.OK).build();

    @GET
    public Response get() {
        return response;
    }
}