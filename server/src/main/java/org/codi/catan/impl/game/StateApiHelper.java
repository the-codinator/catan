/*
 * @author the-codinator
 * created on 2020/5/26
 */

package org.codi.catan.impl.game;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.impl.data.CatanDataConnector;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.Player;
import org.codi.catan.model.game.State;
import org.codi.catan.model.response.StateResponse;

@Singleton
public class StateApiHelper {

    private final GameUtility gameUtility;
    private final CatanDataConnector dataConnector;

    @Inject
    public StateApiHelper(GameUtility gameUtility, CatanDataConnector dataConnector) {
        this.gameUtility = gameUtility;
        this.dataConnector = dataConnector;
    }

    /**
     * Create default game state from a valid {@param board}
     */
    public State createState(Board board) throws CatanException {
        State state = new State(board.getId(), board.getPlayers()[0].getColor(), gameUtility.findDesert(board));
        try {
            dataConnector.createState(state);
        } catch (CatanException e) {
            throw new CatanException("Error creating state", Status.INTERNAL_SERVER_ERROR, e);
        }
        return state;
    }

    /**
     * Create {@link StateResponse} from the overall {@param state} of the game based on the color of the player in the
     * {@param board} with id = {@param userId}
     */
    public StateResponse createStateResponse(State state, Board board, String userId) throws CatanException {
        if (state == null) {
            return null;
        }
        if (board == null) {
            try {
                board = dataConnector.getBoard(state.getId());
            } catch (CatanException e) {
                if (e.getErrorStatus() == Status.NOT_FOUND) {
                    throw new CatanException("Could not find board with id - " + state.getId(), Status.NOT_FOUND, e);
                } else {
                    throw e;
                }
            }
        }
        Color color = null;
        for (Player player : board.getPlayers()) {
            if (player.getId().equals(userId)) {
                color = player.getColor();
                break;
            }
        }
        return new StateResponse(state, color);
    }

    /**
     * Get state with id {@param gameId} and not matching {@param etag}
     */
    public State getState(String gameId, String etag) throws CatanException {
        try {
            return dataConnector.getState(gameId, etag);
        } catch (CatanException e) {
            if (e.getErrorStatus() == Status.NOT_FOUND) {
                throw new CatanException("Could not find state with id - " + gameId, Status.NOT_FOUND, e);
            } else {
                throw e;
            }
        }
    }
}
