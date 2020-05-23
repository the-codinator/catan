/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import org.codi.catan.core.CatanException;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.User;

public interface CatanDataConnector {

    User getUser(User user) throws CatanException;
    void createUser(User user) throws CatanException;
    void updateUser(User user) throws CatanException;
    void deleteUser(User user) throws CatanException;

    Token getToken(Token token) throws CatanException;
    void createToken(Token token) throws CatanException;
    void deleteToken(Token token) throws CatanException;
}
