/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import org.codi.catan.core.CatanException;
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
}
