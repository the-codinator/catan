/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.core.StrongEntity;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.State;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.User;

/**
 * Single GET operations here return requested object if available, throw {@link Status#NOT_FOUND} error otherwise.
 * Batch GET operations return available objects (skip missing objects)
 * For write operations with a conflicting id or missing record, a {@link Status#CONFLICT} (create) or {@link
 * Status#NOT_FOUND} (update/delete) error is thrown.
 * For entities that support ETag via {@link StrongEntity}, and the etag value is present.
 * - read operations use {@code If-None-Match} and return {@code null} if the entity is unchanged. Returned entity will
 * have new etag value set
 * - modify operations use {@code If-Match} and throw a {@link Status#PRECONDITION_FAILED} error if the entity etag did
 * not match.
 * In case of any errors with the underlying data store, a {@code CatanException} with an appropriate message and server
 * side {@link Status} (5xx) error is thrown.
 */
@SuppressWarnings("checkstyle:EmptyLineSeparator")
public interface CatanDataConnector {

    User getUser(String id) throws CatanException;
    User[] getUsers(String... ids) throws CatanException;
    void createUser(User user) throws CatanException;
    void updateUser(User user) throws CatanException;
    void deleteUser(String id) throws CatanException;

    Token getToken(String id) throws CatanException;
    void createToken(Token token) throws CatanException;
    void deleteToken(String id) throws CatanException;

    Board getBoard(String id) throws CatanException;
    void createBoard(Board board) throws CatanException;
    void deleteBoard(String id) throws CatanException;

    State getState(String id, String etag) throws CatanException;
    void createState(State state) throws CatanException;
    void updateState(State state) throws CatanException;
    void deleteState(String id, String etag) throws CatanException;
}
