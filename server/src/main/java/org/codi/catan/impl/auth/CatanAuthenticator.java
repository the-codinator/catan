/*
 * @author the-codinator
 * created on 2020/5/24
 */

package org.codi.catan.impl.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.impl.data.CatanDataConnector;
import org.codi.catan.impl.user.SessionHelper;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.Token.TokenType;
import org.codi.catan.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatanAuthenticator implements Authenticator<Token, User> {

    private static final Logger logger = LoggerFactory.getLogger(CatanAuthenticator.class);

    private final SessionHelper sessionHelper;
    private final CatanDataConnector dataConnector;

    @Inject
    public CatanAuthenticator(SessionHelper sessionHelper, CatanDataConnector dataConnector) {
        this.sessionHelper = sessionHelper;
        this.dataConnector = dataConnector;
    }

    @Override
    public Optional<User> authenticate(Token token) throws AuthenticationException {
        try {
            if (token.getType() != TokenType.access) {
                throw new CatanException("Incorrect Token Type", Status.BAD_REQUEST);
            }
            sessionHelper.validateRequestTokenOffline(token);
            Token dbToken = null;
            try {
                dbToken = dataConnector.getToken(token.getId());
            } catch (CatanException e) {
                if (e.getErrorStatus() != Status.NOT_FOUND) {
                    throw new AuthenticationException("Error reading token data store", e);
                }
            }
            if (!token.equals(dbToken)) {
                throw new CatanException("Invalid Token", Status.BAD_REQUEST);
            }
            User user = new User(token.getUser());
            user.setRoles(token.getRoles());
            return Optional.of(user);
        } catch (CatanException e) {
            logger.warn("Request received with invalid token", e);
            return Optional.empty();
        }
    }
}
