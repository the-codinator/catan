/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.impl.game;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.impl.data.CatanDataConnector;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.Phase;
import org.codi.catan.model.game.State;
import org.codi.catan.model.response.StateResponse;
import org.codi.catan.util.Util;

@Singleton
public class MoveApiHelper {

    private final LayoutHelper layoutHelper;
    private final StateApiHelper stateApiHelper;
    private final GameUtility gameUtility;
    private final CatanDataConnector dataConnector;

    @Inject
    public MoveApiHelper(LayoutHelper layoutHelper, StateApiHelper stateApiHelper, GameUtility gameUtility,
        CatanDataConnector dataConnector) {
        this.layoutHelper = layoutHelper;
        this.stateApiHelper = stateApiHelper;
        this.gameUtility = gameUtility;
        this.dataConnector = dataConnector;
    }

    /**
     * Generic function to play a move
     */
    public <T> StateResponse play(String userId, String gameId, String etag, T request, MoveRequestHandler<T> handler,
        Phase... validPhases) throws CatanException {
        Board board = layoutHelper.getBoard(gameId);
        State state = stateApiHelper.getState(gameId, null);
        if (etag != null && !state.getETag().equals(etag)) {
            throw new CatanException("Someone made a move before you. Please retry with updated game state.",
                Status.PRECONDITION_FAILED);
        }
        gameUtility.ensurePlayerTurn(board, state, userId);
        gameUtility.ensurePhaseForMove(state, validPhases);
        if (handler.shouldValidateInput()) {
            Util.validateInput(request);
        }
        Color color = state.getCurrentMove().getColor();
        handler.play(board, state, request);
        try {
            dataConnector.updateState(state);
        } catch (CatanException e) {
            if (e.getErrorStatus() == Status.PRECONDITION_FAILED) {
                throw new CatanException("Someone made a move before you. Please retry with updated game state.",
                    Status.PRECONDITION_FAILED, e);
            } else {
                throw e;
            }
        }
        return new StateResponse(state, color);
    }

    public StateResponse play(String userId, String gameId, String etag, MoveRequestLessHandler handler,
        Phase... validPhases) throws CatanException {
        return play(userId, gameId, etag, null, handler, validPhases);
    }
}
