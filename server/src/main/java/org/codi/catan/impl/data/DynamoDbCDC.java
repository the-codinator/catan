/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.State;
import org.codi.catan.model.user.Games;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.User;

public class DynamoDbCDC implements CatanDataConnector { // TODO:

    @Override
    public User getUser(String id) throws CatanException {
        return null;
    }

    @Override
    public User[] getUsers(String... ids) throws CatanException {
        return new User[0];
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
    public Games getGames(String id) throws CatanException {
        return null;
    }

    @Override
    public void putGames(Games games) throws CatanException {

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

    @Override
    public Board getBoard(String id) throws CatanException {
        return null;
    }

    @Override
    public void createBoard(Board board) throws CatanException {

    }

    @Override
    public void deleteBoard(String id) throws CatanException {

    }

    @Override
    public State getState(String id, String etag) throws CatanException {
        return null;
    }

    @Override
    public void createState(State state) throws CatanException {

    }

    @Override
    public void updateState(State state) throws CatanException {

    }

    @Override
    public void deleteState(String id, String etag) throws CatanException {

    }
}
