/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import com.codahale.metrics.health.HealthCheck.Result;
import java.util.List;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.State;
import org.codi.catan.model.game.UserGame;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.User;

public class DelegateCDC implements CatanDataConnector {

    private final CatanDataConnector delegate;

    public DelegateCDC(CatanDataConnector delegate) {
        this.delegate = delegate;
    }

    @Override
    public void init() throws CatanException {
        delegate.init();
    }

    @Override
    public Result check() throws CatanException {
        return delegate.check();
    }

    @Override
    public User getUser(String id) throws CatanException {
        return delegate.getUser(id);
    }

    @Override
    public User[] getUsers(String... ids) throws CatanException {
        return delegate.getUsers(ids);
    }

    @Override
    public void createUser(User user) throws CatanException {
        delegate.createUser(user);
    }

    @Override
    public void updateUser(User user) throws CatanException {
        delegate.updateUser(user);
    }

    @Override
    public void deleteUser(String id) throws CatanException {
        delegate.deleteUser(id);
    }

    @Override
    public List<UserGame> getUserGamesByUser(String userId, Boolean ongoing) throws CatanException {
        return delegate.getUserGamesByUser(userId, ongoing);
    }

    @Override
    public void createUserGames(UserGame... userGames) throws CatanException {
        delegate.createUserGames(userGames);
    }

    @Override
    public void updateUserGames(UserGame... userGames) throws CatanException {
        delegate.updateUserGames(userGames);
    }

    @Override
    public void deleteUserGame(String id) throws CatanException {
        delegate.deleteUserGame(id);
    }

    @Override
    public Token getToken(String id) throws CatanException {
        return delegate.getToken(id);
    }

    @Override
    public void createToken(Token token) throws CatanException {
        delegate.createToken(token);
    }

    @Override
    public void deleteToken(String id) throws CatanException {
        delegate.deleteToken(id);
    }

    @Override
    public Board getBoard(String id) throws CatanException {
        return delegate.getBoard(id);
    }

    @Override
    public void createBoard(Board board) throws CatanException {
        delegate.createBoard(board);
    }

    @Override
    public void updateBoard(Board board) throws CatanException {
        delegate.updateBoard(board);
    }

    @Override
    public void deleteBoard(String id) throws CatanException {
        delegate.deleteBoard(id);
    }

    @Override
    public State getState(String id, String etag) throws CatanException {
        return delegate.getState(id, etag);
    }

    @Override
    public void createState(State state) throws CatanException {
        delegate.createState(state);
    }

    @Override
    public void updateState(State state) throws CatanException {
        delegate.updateState(state);
    }

    @Override
    public void deleteState(String id, String etag) throws CatanException {
        delegate.deleteState(id, etag);
    }
}
