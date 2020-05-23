/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import org.codi.catan.core.CatanException;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.User;

@SuppressWarnings("checkstyle:EmptyLineSeparator")
public interface CatanDataConnector {

    User getUser(String id) throws CatanException;
    void createUser(User user) throws CatanException;
    void updateUser(User user) throws CatanException;
    void deleteUser(String id) throws CatanException;

    Token getToken(String id) throws CatanException;
    void createToken(Token token) throws CatanException;
    void deleteToken(String id) throws CatanException;
}
