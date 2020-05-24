/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import org.codi.catan.core.CatanException;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.User;

public class DynamoDbCDC implements CatanDataConnector {

    @Override
    public User getUser(String id) throws CatanException {
        return null;
    }

    @Override
    public boolean createUser(User user) throws CatanException {
        return false;
    }

    @Override
    public boolean updateUser(User user) throws CatanException {
        return false;
    }

    @Override
    public boolean deleteUser(String id) throws CatanException {
        return false;
    }

    @Override
    public Token getToken(String id) throws CatanException {
        return null;
    }

    @Override
    public boolean createToken(Token token) throws CatanException {
        return false;
    }

    @Override
    public boolean deleteToken(String id) throws CatanException {
        return false;
    }
}
