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
import org.codi.catan.impl.handler.BoardStateColorRequestMoveHandler;
import org.codi.catan.impl.handler.BoardStateMoveHandler;
import org.codi.catan.impl.handler.BoardStateRequestMoveHandler;
import org.codi.catan.impl.handler.StateColorRequestMoveHandler;
import org.codi.catan.impl.handler.StateMoveHandler;
import org.codi.catan.impl.handler.StateRequestMoveHandler;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.OutOfTurnApi;
import org.codi.catan.model.game.Phase;
import org.codi.catan.model.game.State;
import org.codi.catan.model.response.StateResponse;
import org.codi.catan.util.Util;

@Singleton
public class MoveApiHelper {

    private final BoardHelper boardHelper;
    private final StateApiHelper stateApiHelper;
    private final GameUtility gameUtility;
    private final CatanDataConnector dataConnector;

    @Inject
    public MoveApiHelper(BoardHelper boardHelper, StateApiHelper stateApiHelper, GameUtility gameUtility,
        CatanDataConnector dataConnector) {
        this.boardHelper = boardHelper;
        this.stateApiHelper = stateApiHelper;
        this.gameUtility = gameUtility;
        this.dataConnector = dataConnector;
    }

    /**
     * Generic function to play a move
     */
    public <T> StateResponse play(OutOfTurnApi outOfTurnApi, String userId, String gameId, String etag, T request,
        BoardStateColorRequestMoveHandler<T> handler, Phase... validPhases) throws CatanException {
        Board board = boardHelper.getBoard(gameId);
        State state = stateApiHelper.getState(gameId, null);
        if (etag != null && !state.getETag().equals(etag)) {
            throw new CatanException("Someone made a move before you. Please retry with updated game state.",
                Status.PRECONDITION_FAILED);
        }
        Color color = gameUtility.checkPlayerTurn(board, state, userId, outOfTurnApi);
        gameUtility.ensurePhaseForMove(state, validPhases);
        if (handler.shouldValidateInput()) {
            Util.validateInput(request);
        }
        handler.play(board, state, color, request);
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

    public <T> StateResponse play(OutOfTurnApi outOfTurnApi, String userId, String gameId, String etag, T request,
        StateColorRequestMoveHandler<T> handler, Phase... validPhases) throws CatanException {
        return play(outOfTurnApi, userId, gameId, etag, request, handler.asBaseType(), validPhases);
    }

    public <T> StateResponse play(String userId, String gameId, String etag, T request,
        BoardStateRequestMoveHandler<T> handler, Phase... validPhases) throws CatanException {
        return play(null, userId, gameId, etag, request, handler.asBaseType(), validPhases);
    }

    public StateResponse play(String userId, String gameId, String etag, BoardStateMoveHandler handler,
        Phase... validPhases) throws CatanException {
        return play(null, userId, gameId, etag, null, handler.asBaseType(), validPhases);
    }

    public <T> StateResponse play(String userId, String gameId, String etag, T request,
        StateRequestMoveHandler<T> handler, Phase... validPhases) throws CatanException {
        return play(null, userId, gameId, etag, request, handler.asBaseType(), validPhases);
    }

    public StateResponse play(String userId, String gameId, String etag, StateMoveHandler handler, Phase... validPhases)
        throws CatanException {
        return play(null, userId, gameId, etag, null, handler.asBaseType(), validPhases);
    }
}
