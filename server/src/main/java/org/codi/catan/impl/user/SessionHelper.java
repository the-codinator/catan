/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.user;

import static org.codi.catan.util.Constants.DAY_MILLIS;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.Token.TokenType;
import org.codi.catan.model.user.User;
import org.codi.catan.util.Util;

@Singleton
public class SessionHelper {

    private final ObjectMapper mapper;

    @Inject
    public SessionHelper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Create a session (and corresponding token) for given {@param user} of type {@param type}
     */
    public Token createSession(User user, TokenType type) throws CatanException {
        if (user == null || user.getId() == null) {
            throw new CatanException("User is null");
        }
        long created = System.currentTimeMillis();
        String id = Util.generateRandomUuid().toString();
        if (type == null) {
            type = TokenType.access;
        }
        switch (type) {
            case refresh:
                return new Token(id, TokenType.refresh, user.getId(), user.getRoles(), created,
                    created + 14 * DAY_MILLIS, null);
            case access:
            default:
                return new Token(id, TokenType.access, user.getId(), user.getRoles(), created, created + DAY_MILLIS,
                    null);
        }
    }

    /**
     * Serialize {@param token} to send to user
     */
    public String serializeToken(Token token) throws CatanException {
        if (token == null) {
            return null;
        }
        try {
            return Util.base64Encode(mapper.writeValueAsString(token));
        } catch (Exception e) {
            throw new CatanException("Error serializing session to token", e);
        }
    }

    /**
     * Deserialize {@param token} received from user
     */
    public Token parseToken(String token) throws CatanException {
        if (token == null) {
            return null;
        }
        try {
            return mapper.readValue(Util.base64Decode(token), Token.class);
        } catch (Exception e) {
            throw new CatanException("Error parsing session from token", e);
        }
    }

    /**
     * Perform offline token validation
     *
     * The following validations are performed:
     * - Token from the future
     * - Expired token
     */
    public void validateRequestTokenOffline(Token token) throws CatanException {
        long now = System.currentTimeMillis();
        if (token.getCreated() > now) {
            throw new CatanException("Session from the future", Status.UNAUTHORIZED);
        }
        if (token.getExpires() < now) {
            throw new CatanException("Session has expired", Status.UNAUTHORIZED);
        }
    }
}
