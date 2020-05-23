/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.User;
import org.codi.catan.util.Util;

public class ImMemoryCDC implements CatanDataConnector {

    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, Token> tokens = new ConcurrentHashMap<>();

    @Override
    public User getUser(User user) throws CatanException {
        User u = users.get(user.getId());
        if (u == null) {
            throw new CatanException("User does not exist", Status.NOT_FOUND);
        }
        return u;
    }

    @Override
    public void createUser(User user) throws CatanException {
        if (users.putIfAbsent(user.getId(), user) != null) {
            throw new CatanException("User id is already taken", Status.CONFLICT);
        }
    }

    @Override
    public void updateUser(User user) throws CatanException {
        if (users.computeIfPresent(user.getId(), (k, v) -> user) == null) {
            throw new CatanException("User does not exist", Status.BAD_REQUEST);
        }
    }

    @Override
    public void deleteUser(User user) throws CatanException {
        if (users.remove(user.getId()) == null) {
            throw new CatanException("User does not exist", Status.NOT_FOUND);
        }
    }

    @Override
    public Token getToken(Token token) throws CatanException {
        Token t = tokens.get(token.getId());
        if (t == null) {
            throw new CatanException("Session does not exist", Status.NOT_FOUND);
        }
        return t;
    }

    @Override
    public void createToken(Token token) throws CatanException {
        if (tokens.putIfAbsent(token.getId(), token) != null) {
            throw new CatanException("Session already exists", Status.CONFLICT);
        }
    }

    @Override
    public void deleteToken(Token token) throws CatanException {
        if (tokens.remove(token.getId()) == null) {
            throw new CatanException("Session does not exist", Status.NOT_FOUND);
        }
    }
}
