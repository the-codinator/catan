/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import static org.codi.catan.util.Constants.ENTITY_CONFLICT;
import static org.codi.catan.util.Constants.ENTITY_NOT_FOUND;
import static org.codi.catan.util.Constants.ENTITY_PRECONDITION_FAILED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.core.IdentifiableEntity;
import org.codi.catan.model.core.StrongEntity;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.State;
import org.codi.catan.model.user.Games;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.User;
import org.codi.catan.util.Util;

public class InMemoryCDC implements CatanDataConnector {

    private final ObjectMapper objectMapper;
    private final LoadingCache<Class<? extends IdentifiableEntity>, Map<String, String>> db;

    @Inject
    public InMemoryCDC(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        db = CacheBuilder.newBuilder().build(CacheLoader.from((Supplier<Map<String, String>>) ConcurrentHashMap::new));
    }

    public void reset() {
        db.invalidateAll();
    }

    private String generateETag(String serialized) {
        // Make something that can easily be treated as opaque, but is a consistent algorithm (idempotent)
        return serialized == null ? null : Util.base64Encode(String.valueOf(serialized.hashCode()));
    }

    private <T extends IdentifiableEntity> T get(Class<T> clazz, String id, String etag) throws CatanException {
        try {
            String serialized = db.get(clazz).get(id);
            if (serialized == null) {
                throw new CatanException(String.format(ENTITY_NOT_FOUND, clazz.getSimpleName(), id), Status.NOT_FOUND);
            }
            T value = objectMapper.readValue(serialized, clazz);
            if (StrongEntity.class.isAssignableFrom(clazz)) {
                StrongEntity seValue = (StrongEntity) value;
                String generatedETag = generateETag(serialized);
                if (generatedETag.equals(etag)) {
                    value = null;
                } else {
                    seValue.setETag(generatedETag);
                }
            }
            return value;
        } catch (ExecutionException | JsonProcessingException e) {
            throw new CatanException("DB error", e);
        }
    }

    private <T extends IdentifiableEntity> void create(Class<T> clazz, T value) throws CatanException {
        try {
            String serialized = objectMapper.writeValueAsString(value);
            String oldValue = db.get(clazz).putIfAbsent(value.getId(), serialized);
            if (oldValue == null) {
                if (StrongEntity.class.isAssignableFrom(clazz)) {
                    ((StrongEntity) value).setETag(generateETag(serialized));
                }
            } else {
                throw new CatanException(String.format(ENTITY_CONFLICT, clazz.getSimpleName(), value.getId()),
                    Status.CONFLICT);
            }
        } catch (ExecutionException | JsonProcessingException e) {
            throw new CatanException("DB error", e);
        }
    }

    private <T extends IdentifiableEntity> void update(Class<T> clazz, T value) throws CatanException {
        try {
            String serialized = objectMapper.writeValueAsString(value);
            if (StrongEntity.class.isAssignableFrom(clazz)) {
                StrongEntity seValue = (StrongEntity) value;
                if (seValue.getETag() != null) {
                    String result = db.get(clazz).computeIfPresent(value.getId(), (k, oldValue) -> {
                        String oldEtag = generateETag(oldValue);
                        if (seValue.getETag().equals(oldEtag)) {
                            seValue.setETag(generateETag(serialized));
                            return serialized;
                        } else {
                            return oldValue;
                        }
                    });
                    if (result == null) {
                        throw new CatanException(String.format(ENTITY_NOT_FOUND, clazz.getSimpleName(), value.getId()),
                            Status.NOT_FOUND);
                    }
                    //noinspection StringEquality
                    if (result != serialized) {
                        throw new CatanException(
                            String.format(ENTITY_PRECONDITION_FAILED, clazz.getSimpleName(), value.getId()),
                            Status.PRECONDITION_FAILED);
                    }
                    // result == serialized
                    return;
                }
            }
            // Other cases
            if (db.get(clazz).replace(value.getId(), serialized) == null) {
                throw new CatanException(String.format(ENTITY_NOT_FOUND, clazz.getSimpleName(), value.getId()),
                    Status.NOT_FOUND);
            }
        } catch (ExecutionException | JsonProcessingException e) {
            throw new CatanException("DB error", e);
        }
    }

    private <T extends IdentifiableEntity> void put(Class<T> clazz, T value) throws CatanException {
        try {
            String serialized = objectMapper.writeValueAsString(value);
            db.get(clazz).put(value.getId(), serialized);
        } catch (ExecutionException | JsonProcessingException e) {
            throw new CatanException("DB error", e);
        }
    }

    private <T extends IdentifiableEntity> void delete(Class<T> clazz, String id) throws CatanException {
        try {
            if (db.get(clazz).remove(id) == null) {
                throw new CatanException(String.format(ENTITY_NOT_FOUND, clazz.getSimpleName(), id), Status.NOT_FOUND);
            }
        } catch (ExecutionException e) {
            throw new CatanException("DB error", e);
        }
    }

    @Override
    public User getUser(String id) throws CatanException {
        return get(User.class, id, null);
    }

    @Override
    public User[] getUsers(String... ids) throws CatanException {
        List<User> users = new ArrayList<>(ids.length);
        for (String id : ids) {
            try {
                users.add(getUser(id));
            } catch (CatanException e) {
                if (e.getErrorStatus() != Status.NOT_FOUND) {
                    throw e;
                }
            }
        }
        return users.toArray(new User[0]);
    }

    @Override
    public void createUser(User user) throws CatanException {
        create(User.class, user);
    }

    @Override
    public void updateUser(User user) throws CatanException {
        update(User.class, user);
    }

    @Override
    public void deleteUser(String id) throws CatanException {
        delete(User.class, id);
    }

    @Override
    public Games getGames(String id) throws CatanException {
        return get(Games.class, id, null);
    }

    @Override
    public void putGames(Games games) throws CatanException {
        put(Games.class, games);
    }

    @Override
    public Token getToken(String id) throws CatanException {
        return get(Token.class, id, null);
    }

    @Override
    public void createToken(Token token) throws CatanException {
        create(Token.class, token);
    }

    @Override
    public void deleteToken(String id) throws CatanException {
        delete(Token.class, id);
    }

    @Override
    public Board getBoard(String id) throws CatanException {
        return get(Board.class, id, null);
    }

    @Override
    public void createBoard(Board board) throws CatanException {
        create(Board.class, board);
    }

    @Override
    public void deleteBoard(String id) throws CatanException {
        delete(Board.class, id);
    }

    @Override
    public State getState(String id, String etag) throws CatanException {
        return get(State.class, id, etag);
    }

    @Override
    public void createState(State state) throws CatanException {
        create(State.class, state);
    }

    @Override
    public void updateState(State state) throws CatanException {
        update(State.class, state);
    }

    @Override
    public void deleteState(String id, String etag) throws CatanException {
        delete(State.class, id);
    }
}
