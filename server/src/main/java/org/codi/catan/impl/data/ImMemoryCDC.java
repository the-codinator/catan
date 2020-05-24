/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.User;

public class ImMemoryCDC implements CatanDataConnector {

    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, Token> tokens = new ConcurrentHashMap<>();

    @Override
    public User getUser(String id) {
        return users.get(id);
    }

    @Override
    public boolean createUser(User user) {
        return users.putIfAbsent(user.getId(), user) == null;
    }

    @Override
    public boolean updateUser(User user) {
        return users.computeIfPresent(user.getId(), (k, v) -> user) != null;
    }

    @Override
    public boolean deleteUser(String id) {
        return users.remove(id) != null;
    }

    @Override
    public Token getToken(String id) {
        return tokens.get(id);
    }

    @Override
    public boolean createToken(Token token) {
        return tokens.putIfAbsent(token.getId(), token) == null;
    }

    @Override
    public boolean deleteToken(String id) {
        return tokens.remove(id) == null;
    }
}
