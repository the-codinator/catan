/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.filter;

import static org.codi.catan.util.Constants.HEADER_REQUEST_ID;
import static org.codi.catan.util.Constants.TOKEN;
import static org.codi.catan.util.Util.shouldSkipFilters;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import org.codi.catan.model.user.Token;
import org.codi.catan.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class RequestIdAndAccessLogFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestIdAndAccessLogFilter.class);

    @Override
    public void filter(ContainerRequestContext request) {
        if (shouldSkipFilters(request)) {
            return;
        }
        String id = request.getHeaderString(HEADER_REQUEST_ID);
        if (id == null || id.isBlank()) {
            id = Util.generateRandomUuid();
            request.getHeaders().putSingle(HEADER_REQUEST_ID, id);
        }
        MDC.put("requestId", id);
        Token token = (Token) request.getProperty(TOKEN);
        String user = token == null ? "" : token.getUser();
        logger.debug("[ ACCESS ] method={} path=/{} user={}", request.getMethod(), request.getUriInfo().getPath(),
            user);
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        if (shouldSkipFilters(request)) {
            return;
        }
        String id = request.getHeaderString(HEADER_REQUEST_ID);
        response.getHeaders().putSingle(HEADER_REQUEST_ID, id);
    }
}
