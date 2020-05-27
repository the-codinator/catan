/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.IdentifiableEntity;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.User;

public class ImMemoryCDC implements CatanDataConnector {

    private static final List<Class<? extends IdentifiableEntity>> types = List.of(User.class, Token.class,
        Board.class);

    private final ObjectMapper objectMapper;
    private final Map<Class<? extends IdentifiableEntity>, Map<String, String>> db;

    @Inject
    public ImMemoryCDC(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        db = new ConcurrentHashMap<>();
        for (var clazz : types) {
            db.put(clazz, new ConcurrentHashMap<>());
        }
    }

    private <T extends IdentifiableEntity> T get(Class<T> clazz, String id) throws CatanException {
        try {
            String value = db.get(clazz).get(id);
            return value == null ? null : objectMapper.readValue(value, clazz);
        } catch (JsonProcessingException e) {
            throw new CatanException("Json error", e);
        }
    }

    private <T extends IdentifiableEntity> boolean create(Class<T> clazz, T value) throws CatanException {
        try {
            return db.get(clazz).putIfAbsent(value.getId(), objectMapper.writeValueAsString(value)) == null;
        } catch (JsonProcessingException e) {
            throw new CatanException("Json error", e);
        }
    }

    private <T extends IdentifiableEntity> boolean update(Class<T> clazz, T value) throws CatanException {
        try {
            String serializedValue = objectMapper.writeValueAsString(value);
            return db.get(clazz).computeIfPresent(value.getId(), (k, v) -> serializedValue) != null;
        } catch (JsonProcessingException e) {
            throw new CatanException("Json error", e);
        }
    }

    private <T> boolean delete(Class<T> clazz, String id) {
        return db.get(clazz).remove(id) != null;
    }

    @Override
    public User getUser(String id) throws CatanException {
        return get(User.class, id);
    }

    @Override
    public User[] getUsers(String... ids) throws CatanException {
        List<User> users = new ArrayList<>(ids.length);
        for (String id : ids) {
            User user = getUser(id);
            if (user != null) {
                users.add(user);
            }
        }
        return users.toArray(new User[0]);
    }

    @Override
    public boolean createUser(User user) throws CatanException {
        return create(User.class, user);
    }

    @Override
    public boolean updateUser(User user) throws CatanException {
        return update(User.class, user);
    }

    @Override
    public boolean deleteUser(String id) {
        return delete(User.class, id);
    }

    @Override
    public Token getToken(String id) throws CatanException {
        return get(Token.class, id);
    }

    @Override
    public boolean createToken(Token token) throws CatanException {
        return create(Token.class, token);
    }

    @Override
    public boolean deleteToken(String id) {
        return delete(Token.class, id);
    }

    @Override
    public Board getBoard(String id) throws CatanException {
        return get(Board.class, id);
    }

    @Override
    public boolean createBoard(Board board) throws CatanException {
        return create(Board.class, board);
    }

    @Override
    public boolean deleteBoard(String id) {
        return delete(Board.class, id);
    }
}
