/*
 * @author the-codinator
 * created on 2020/5/23
 */

package org.codi.catan.core;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CatanExceptionMapper implements ExceptionMapper<CatanException> {

    @Override
    public Response toResponse(CatanException e) {
        return Response.status(e.getErrorStatus().getStatusCode())
            .entity(e.asMessageResponse())
            .type(MediaType.APPLICATION_JSON)
            .build();

    }
}
