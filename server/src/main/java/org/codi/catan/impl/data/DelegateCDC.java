/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.User;

public class DelegateCDC implements CatanDataConnector {

    protected final CatanDataConnector delegate;

    public DelegateCDC(CatanDataConnector delegate) {
        this.delegate = delegate;
    }

    @Override
    public User getUser(String id) throws CatanException {
        return delegate.getUser(id);
    }

    @Override
    public boolean createUser(User user) throws CatanException {
        return delegate.createUser(user);
    }

    @Override
    public boolean updateUser(User user) throws CatanException {
        return delegate.updateUser(user);
    }

    @Override
    public boolean deleteUser(String id) throws CatanException {
        return delegate.deleteUser(id);
    }

    @Override
    public Token getToken(String id) throws CatanException {
        return delegate.getToken(id);
    }

    @Override
    public boolean createToken(Token token) throws CatanException {
        return delegate.createToken(token);
    }

    @Override
    public boolean deleteToken(String id) throws CatanException {
        return delegate.deleteToken(id);
    }

    @Override
    public Board getBoard(String id) throws CatanException {
        return delegate.getBoard(id);
    }

    @Override
    public boolean createBoard(Board board) throws CatanException {
        return delegate.createBoard(board);
    }

    @Override
    public boolean deleteBoard(String id) throws CatanException {
        return delegate.deleteBoard(id);
    }
}
