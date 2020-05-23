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

    public User getUser(User user) throws CatanException {
        return delegate.getUser(user);
    }

    public void createUser(User user) throws CatanException {
        delegate.createUser(user);
    }

    public void updateUser(User user) throws CatanException {
        delegate.updateUser(user);
    }

    public void deleteUser(User user) throws CatanException {
        delegate.deleteUser(user);
    }

    public Token getToken(Token token) throws CatanException {
        return delegate.getToken(token);
    }

    public void createToken(Token token) throws CatanException {
        delegate.createToken(token);
    }

    public void deleteToken(Token token) throws CatanException {
        delegate.deleteToken(token);
    }
}
