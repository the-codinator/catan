package org.codi.catan.util;

import javax.ws.rs.container.ContainerRequestContext;

public class Util {

    public static boolean shouldSkipFilters(ContainerRequestContext request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod()) || "ping".equals(request.getUriInfo().getPath());
    }
}
