/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.User;

/**
 * All operations here return requested object if available, {@code null} otherwise.
 * For operations that do not request resources, a {@code boolean} representing successful operation is returned.
 * In case of any errors with the underlying data store, a {@code CatanException} with an appropriate message is thrown.
 */
@SuppressWarnings("checkstyle:EmptyLineSeparator")
public interface CatanDataConnector {

    User getUser(String id) throws CatanException;
    boolean createUser(User user) throws CatanException;
    boolean updateUser(User user) throws CatanException;
    boolean deleteUser(String id) throws CatanException;

    Token getToken(String id) throws CatanException;
    boolean createToken(Token token) throws CatanException;
    boolean deleteToken(String id) throws CatanException;

    Board getBoard(String id) throws CatanException;
    boolean createBoard(Board board) throws CatanException;
    boolean deleteBoard(String id) throws CatanException;
}
