/*
 * @author the-codinator
 * created on 2020/5/28
 */

package org.codi.catan.filter;

import static org.codi.catan.util.Constants.HEADER_ETAG;
import static org.codi.catan.util.Constants.HEADER_IF_NONE_MATCH;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Priority;
import javax.ws.rs.NameBinding;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import org.codi.catan.filter.ETagHeaderFilter.ETagHeaderSupport;
import org.codi.catan.model.core.StrongEntity;
import org.codi.catan.util.Util;

@Provider
@Priority(Priorities.HEADER_DECORATOR - 1)
@ETagHeaderSupport
public class ETagHeaderFilter implements ContainerResponseFilter {

    @NameBinding
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(value = RetentionPolicy.RUNTIME)
    public @interface ETagHeaderSupport {

    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        if (!Util.isOkStatus(response.getStatus())) {
            return;
        }
        String etag = null;
        if (response.hasEntity()) {
            if (response.getEntity() instanceof StrongEntity) {
                etag = ((StrongEntity) response.getEntity()).getETag();
            }
        } else {
            etag = request.getHeaders().getFirst(HEADER_IF_NONE_MATCH);
        }
        if (etag != null) {
            response.getHeaders().putSingle(HEADER_ETAG, etag);
        }
    }
}
