package org.codi.catan.filter;

import static org.codi.catan.util.Constants.REQUEST_START_TIME;
import static org.codi.catan.util.Util.shouldSkipFilters;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(Integer.MAX_VALUE)
public class ResponseLogAndTimingFilter implements ContainerResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(ResponseLogAndTimingFilter.class);

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        if (shouldSkipFilters(request)) {
            return;
        }
        long duration = System.currentTimeMillis() - (Long) request.getProperty(REQUEST_START_TIME);
        response.getHeaders().putSingle("Server-Timing", "total;dur=" + duration);
        logger.debug("[ REQUEST ] status={} duration={}", response.getStatus(), duration);
    }
}
