/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.Token;
import org.codi.catan.model.User;

public class ImMemoryCDC implements CatanDataConnector {

    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, Token> tokens = new ConcurrentHashMap<>();

    @Override
    public User getUser(String id) throws CatanException {
        User user = users.get(id);
        if (user == null) {
            throw new CatanException("User does not exist", Status.NOT_FOUND);
        }
        return user;
    }

    @Override
    public void createUser(User user) throws CatanException {
        if (users.putIfAbsent(user.getId(), user) != null) {
            throw new CatanException("User already exists", Status.CONFLICT);
        }
    }

    @Override
    public void updateUser(User user) throws CatanException {
        if (users.computeIfPresent(user.getId(), (k, v) -> user) == null) {
            throw new CatanException("User does not exist", Status.BAD_REQUEST);
        }
    }

    @Override
    public void deleteUser(String id) throws CatanException {
        if (users.remove(id) == null) {
            throw new CatanException("User does not exist", Status.NOT_FOUND);
        }
    }

    @Override
    public Token getToken(String id) throws CatanException {
        Token token = tokens.get(id);
        if (token == null) {
            throw new CatanException("Session does not exist", Status.NOT_FOUND);
        }
        return token;
    }

    @Override
    public void createToken(Token token) throws CatanException {
        if (tokens.putIfAbsent(token.getId(), token) != null) {
            throw new CatanException("Session already exists", Status.CONFLICT);
        }
    }

    @Override
    public void deleteToken(String id) throws CatanException {
        if (tokens.remove(id) == null) {
            throw new CatanException("Session does not exist", Status.NOT_FOUND);
        }
    }
}
