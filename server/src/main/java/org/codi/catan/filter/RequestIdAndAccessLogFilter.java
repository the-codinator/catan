package org.codi.catan.filter;

import static org.codi.catan.util.Constants.REQUEST_ID;
import static org.codi.catan.util.Constants.REQUEST_START_TIME;
import static org.codi.catan.util.Util.shouldSkipFilters;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
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
        String id = request.getHeaderString(REQUEST_ID);
        if (id == null || id.isBlank()) {
            id = generateRandomUuid().toString();
            request.getHeaders().putSingle(REQUEST_ID, id);
        }
        MDC.put("requestId", id);
        logger.debug("[ ACCESS ] method={} path={} start={}", request.getMethod(), request.getUriInfo().getPath(),
            request.getProperty(REQUEST_START_TIME));
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        if (shouldSkipFilters(request)) {
            return;
        }
        String id = request.getHeaderString(REQUEST_ID);
        response.getHeaders().putSingle(REQUEST_ID, id);
    }

    /**
     * Copied from {@link io.dropwizard.jersey.filter.RequestIdFilter}
     */
    private static UUID generateRandomUuid() {
        final Random rnd = ThreadLocalRandom.current();
        long mostSig = rnd.nextLong();
        long leastSig = rnd.nextLong();

        // Identify this as a version 4 UUID, that is one based on a random value.
        mostSig &= 0xffffffffffff0fffL;
        mostSig |= 0x0000000000004000L;

        // Set the variant identifier as specified for version 4 UUID values.  The two
        // high order bits of the lower word are required to be one and zero, respectively.
        leastSig &= 0x3fffffffffffffffL;
        leastSig |= 0x8000000000000000L;

        return new UUID(mostSig, leastSig);
    }
}
