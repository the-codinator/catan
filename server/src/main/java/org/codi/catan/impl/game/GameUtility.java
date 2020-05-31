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
import org.codi.catan.model.game.Hand;
import org.codi.catan.model.game.House;
import org.codi.catan.model.game.Phase;
import org.codi.catan.model.game.Player;
import org.codi.catan.model.game.Resource;
import org.codi.catan.model.game.State;
import org.codi.catan.util.Util;

@Singleton
public class GameUtility {

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

    /**
     * Find the file position of the thief on the {@param board}
     */
    public int findDesert(Board board) throws CatanException {
        int desert = Util.find(board.getTiles(), t -> t.getResource() == null);
        if (desert == -1) {
            throw new CatanException("Invalid Board - Desert not found");
        }
        return desert;
    }

    public House getHouseOnVertex(State state, int vertex) {
        return Util.find(state.getHouses(), h -> h.getVertex() == vertex);
    }

    /**
     * Gain {@param count} of {@param resource} from the bank
     * If bank does not have enough resources, bank will provide only as much is available
     * Positive count indicates player is earning from bank
     * Negative count indicates player is spending to bank
     */
    public void transferResourcesWithBank(State state, Resource resource, int count) throws CatanException {
        if (resource == null) {
            throw new CatanException("Attempting to transfer null resource");
        }
        if (count == 0) {
            return;
        }
        Hand hand = state.getHand(state.getCurrentMove().getColor());
        if (count > 0) {
            count = Math.min(count, state.getBank().get(resource));
        } else {
            if (hand.getResources().get(resource) < -count) {
                throw new CatanException("Not enough [" + resource + "] resources available in hand",
                    Status.BAD_REQUEST);
            }
        }
        Util.addToFrequencyMap(hand.getResources(), resource, count);
        Util.addToFrequencyMap(state.getBank(), resource, -count);
    }
}
