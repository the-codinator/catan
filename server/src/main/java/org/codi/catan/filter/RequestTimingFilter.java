package org.codi.catan.filter;

import static org.codi.catan.util.Constants.REQUEST_START_TIME;
import static org.codi.catan.util.Util.shouldSkipFilters;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Integer.MIN_VALUE)
public class RequestTimingFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext request) {
        if (shouldSkipFilters(request)) {
            return;
        }
        request.setProperty(REQUEST_START_TIME, System.currentTimeMillis());
    }
}
