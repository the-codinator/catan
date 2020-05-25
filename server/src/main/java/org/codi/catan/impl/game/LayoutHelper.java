/*
 * @author the-codinator
 * created on 2020/5/26
 */

package org.codi.catan.impl.game;

import com.google.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.impl.data.CatanDataConnector;
import org.codi.catan.model.game.Board;
import org.codi.catan.util.Util;

@Singleton
public class LayoutHelper {

    private final CatanDataConnector dataConnector;

    @Inject
    public LayoutHelper(CatanDataConnector dataConnector) {
        this.dataConnector = dataConnector;
    }

    private void validateBoard(Board board) throws CatanException {
        board.setId(Util.generateRandomUuid().toString());
        // TODO:
    }

    /**
     * Create {@param board} for a new game
     */
    public Board create(Board board) throws CatanException {
        Util.validateInput(board);
        validateBoard(board);
        if (!dataConnector.createBoard(board)) {
            throw new CatanException("Error creating game - conflicting id");
        }
        return board;
    }

    public Board getBoard(String gameId) throws CatanException {
        Board board = dataConnector.getBoard(gameId);
        if (board == null) {
            throw new CatanException("Game not found", Status.NOT_FOUND);
        }
        return board;
    }
}
