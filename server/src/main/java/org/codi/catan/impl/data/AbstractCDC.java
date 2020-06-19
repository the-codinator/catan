/*
 * @author the-codinator
 * created on 2020/6/19
 */

package org.codi.catan.impl.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.core.IdentifiableEntity;
import org.codi.catan.model.core.StrongEntity;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.State;
import org.codi.catan.model.user.Games;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCDC implements CatanDataConnector {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Get entity of type {@param clazz} by {@param id} if {@param etag} does not match
     *
     * @throws CatanException Underlying db error OR entity not found (errorStatus = {@link Status#NOT_FOUND})
     */
    protected abstract <T extends IdentifiableEntity> T get(Class<T> clazz, String id, String etag)
        throws CatanException;

    /**
     * Get entity of type {@param clazz} by {@param id}
     * Does not support {@code etag} from {@link StrongEntity}
     *
     * @throws CatanException Underlying db error OR entity not found (errorStatus = {@link Status#NOT_FOUND})
     */
    @SuppressWarnings("unchecked")
    protected <T extends IdentifiableEntity> T[] getBatch(Class<T> clazz, String... ids) throws CatanException {
        List<T> list = new ArrayList<>(ids.length);
        for (String id : ids) {
            try {
                list.add(get(clazz, id, null));
            } catch (CatanException e) {
                if (e.getErrorStatus() != Status.NOT_FOUND) {
                    throw e;
                }
            }
        }
        return list.toArray((T[]) Array.newInstance(clazz, 0));
    }

    /**
     * Create entity {@param value} of type {@param clazz}
     *
     * @throws CatanException Underlying db error OR entity already exists found (errorStatus = {@link Status#CONFLICT})
     */
    protected abstract <T extends IdentifiableEntity> void create(Class<T> clazz, T value) throws CatanException;

    /**
     * Update entity {@param value} of type {@param clazz}
     * Honors etag match if sub-type of {@link StrongEntity} and etag is set
     *
     * @throws CatanException Underlying db error OR entity not found (errorStatus = {@link Status#NOT_FOUND})
     */
    protected abstract <T extends IdentifiableEntity> void update(Class<T> clazz, T value) throws CatanException;

    /**
     * Create / Overwrite entity {@param value} of type {@param clazz}
     * Does not support {@code etag} from {@link StrongEntity}
     *
     * @throws CatanException Underlying db error
     */
    protected abstract <T extends IdentifiableEntity> void put(Class<T> clazz, T value) throws CatanException;

    /**
     * Get entity of type {@param clazz} by {@param id}
     * Does not honor {@code etag} from {@link StrongEntity}
     *
     * @throws CatanException Underlying db error OR entity not found (errorStatus = {@link Status#NOT_FOUND})
     */
    protected abstract <T extends IdentifiableEntity> void delete(Class<T> clazz, String id) throws CatanException;

    @Override
    public User getUser(String id) throws CatanException {
        return get(User.class, id, null);
    }

    @Override
    public User[] getUsers(String... ids) throws CatanException {
        return getBatch(User.class, ids);
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
