/*
 * @author the-codinator
 * created on 2020/5/26
 */

package org.codi.catan.impl.game;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.codi.catan.impl.data.CatanDataConnector;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.State;

@Singleton
public class StateHelper { // TODO:

    private final CatanDataConnector dataConnector;

    @Inject
    public StateHelper(CatanDataConnector dataConnector) {
        this.dataConnector = dataConnector;
    }

    public State createState(Board board) {
        return null;
    }

    public State getState(String etag, String gameId) {
        return null;
    }

    public void validateState(State state) {
    }
}
