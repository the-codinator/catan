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
    public User getUser(User user) throws CatanException {
        return null;
    }

    @Override
    public void createUser(User user) throws CatanException {

    }

    @Override
    public void updateUser(User user) throws CatanException {

    }

    @Override
    public void deleteUser(User user) throws CatanException {

    }

    @Override
    public Token getToken(Token token) throws CatanException {
        return null;
    }

    @Override
    public void createToken(Token token) throws CatanException {

    }

    @Override
    public void deleteToken(Token token) throws CatanException {

    }
}
