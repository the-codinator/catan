/*
 * @author the-codinator
 * created on 2020/5/24
 */

package org.codi.catan.filter;

import static org.codi.catan.util.Constants.BEARER_PREFIX;
import static org.codi.catan.util.Constants.HEADER_AUTHORIZATION;
import static org.codi.catan.util.Constants.TOKEN;

import com.google.inject.Inject;
import io.dropwizard.auth.AuthFilter;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import org.codi.catan.core.CatanException;
import org.codi.catan.impl.auth.CachingCatanAuthenticator;
import org.codi.catan.impl.auth.CatanAuthorizer;
import org.codi.catan.impl.user.SessionHelper;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.User;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class CatanAuthFilter extends AuthFilter<Token, User> {

    private final SessionHelper sessionHelper;

    @Inject
    public CatanAuthFilter(SessionHelper sessionHelper, CachingCatanAuthenticator authenticator,
        CatanAuthorizer authorizer) {
        this.sessionHelper = sessionHelper;
        this.authenticator = authenticator;
        this.authorizer = authorizer;
        this.prefix = "Bearer";
        this.realm = "catan";
    }

    @Override
    public void filter(ContainerRequestContext request) {
        Token token = null;
        try {
            token = getTokenFromHeader(request);
        } catch (CatanException e) {
            logger.warn("Error performing authentication", e);
        }
        if (!authenticate(request, token, "BEARER")) {
            throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
        }
        request.setProperty(TOKEN, token);
    }

    private Token getTokenFromHeader(ContainerRequestContext request) throws CatanException {
        String authHeader = request.getHeaders().getFirst(HEADER_AUTHORIZATION);
        if (authHeader == null) {
            throw new CatanException("Missing authorization header", Status.UNAUTHORIZED);
        }
        if (!authHeader.startsWith(BEARER_PREFIX)) {
            throw new CatanException("Bad authorization header format");
        }
        String token = authHeader.substring(BEARER_PREFIX.length());
        return sessionHelper.parseToken(token);
    }
}
