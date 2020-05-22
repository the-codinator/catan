/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import org.codi.catan.core.CatanException;
import org.codi.catan.model.Token;
import org.codi.catan.model.User;

public class DynamoDbCDC implements CatanDataConnector {

    @Override
    public User getUser(String id) throws CatanException {
        return null;
    }

    @Override
    public void createUser(User user) throws CatanException {

    }

    @Override
    public void updateUser(User user) throws CatanException {

    }

    @Override
    public void deleteUser(String id) throws CatanException {

    }

    @Override
    public Token getToken(String id) throws CatanException {
        return null;
    }

    @Override
    public void createToken(Token token) throws CatanException {

    }

    @Override
    public void deleteToken(String id) throws CatanException {

    }
}
