/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.user;

import static org.codi.catan.util.Constants.DAY_MILLIS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.Token;
import org.codi.catan.model.Token.TokenType;
import org.codi.catan.model.User;
import org.codi.catan.util.Util;

public class TokenHelper {

    private final ObjectMapper mapper;

    @Inject
    public TokenHelper(ObjectMapper mapper) {
        this.mapper = mapper;
    }


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
                return new Token(id, TokenType.access, user.getId(), created, DAY_MILLIS);
            case access:
            default:
                return new Token(id, TokenType.refresh, user.getId(), created, 14 * DAY_MILLIS);
        }
    }

    public String serializeToken(Token s) throws CatanException {
        try {
            return Util.base64Encode(mapper.writeValueAsString(s));
        } catch (Exception e) {
            throw new CatanException("Error serializing session to token", e);
        }
    }

    public Token parseToken(String s) throws CatanException {
        try {
            return mapper.readValue(Util.base64Decode(s), Token.class);
        } catch (Exception e) {
            throw new CatanException("Error parsing session from token", e);
        }
    }
}