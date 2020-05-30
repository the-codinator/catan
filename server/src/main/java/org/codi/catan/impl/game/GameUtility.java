/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.impl.game;

import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.Phase;
import org.codi.catan.model.game.Player;
import org.codi.catan.model.game.State;
import org.codi.catan.model.game.Tile;

@Singleton
public class GameUtility {

    /**
     * Find the file position of the thief on the {@param board}
     */
    public int findThief(Board board) throws CatanException {
        Tile[] tiles = board.getTiles();
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i].getResource() == null) {
                return i;
            }
        }
        throw new CatanException("Invalid Board");
    }

    /**
     * Validates that the current turn belongs specified {@param user}
     */
    public void ensurePlayerTurn(Board board, State state, String user) throws CatanException {
        Color color = state.getCurrentMove().getColor();
        for (Player player : board.getPlayers()) {
            if (player.getId().equals(user) && player.getColor() == color) {
                return;
            }
        }
        throw new CatanException("Cannot play this move out of turn", Status.FORBIDDEN);
    }

    /**
     * Validate that a move is played in appropriate phases
     */
    public void ensurePhaseForMove(State state, Phase... validPhases) throws CatanException {
        Phase phase = state.getPhase();
        for (Phase p : validPhases) {
            if (p == phase) {
                return;
            }
        }
        throw new CatanException("Cannot play this move in [" + phase.toString() + "] phase", Status.FORBIDDEN);
    }
}
