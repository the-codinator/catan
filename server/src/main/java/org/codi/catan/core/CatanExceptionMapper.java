/*
 * @author the-codinator
 * created on 2020/5/23
 */

package org.codi.catan.core;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class CatanExceptionMapper implements ExceptionMapper<CatanException> {

    private static final Logger logger = LoggerFactory.getLogger(CatanException.class);

    @Override
    public Response toResponse(CatanException e) {
        switch (e.getErrorStatus().getStatusCode() / 100) {
            case 4:
                logger.warn("Request failed with client error", e);
                break;
            case 5:
                logger.error("Request failed with server error", e);
                break;
            default:
        }
        return Response.status(e.getErrorStatus().getStatusCode())
            .entity(e.asMessageResponse())
            .type(MediaType.APPLICATION_JSON)
            .build();

    }
}
