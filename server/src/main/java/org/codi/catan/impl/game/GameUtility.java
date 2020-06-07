/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.impl.game;

import static org.codi.catan.util.Constants.MAX_ROLL_PER_DIE;
import static org.codi.catan.util.Constants.MIN_ROLL_PER_DIE;

import java.util.Random;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.BadRequestException;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.Hand;
import org.codi.catan.model.game.OutOfTurnApi;
import org.codi.catan.model.game.Phase;
import org.codi.catan.model.game.Player;
import org.codi.catan.model.game.Resource;
import org.codi.catan.model.game.State;
import org.codi.catan.model.game.Tile;
import org.codi.catan.util.Util;

@Singleton
public class GameUtility {

    private static final int[] EMPTY_INT_ARRAY = new int[0];

    private final Random random = new Random();

    /**
     * Validates that the current turn belongs specified {@param user}
     *
     * @return Color of the player making the request
     */
    public Color checkPlayerTurn(Board board, State state, String user, OutOfTurnApi outOfTurnApi)
        throws CatanException {
        Color color = null;
        // Move by actual player whose turn it is
        for (Player player : board.getPlayers()) {
            if (player.getId().equals(user)) {
                color = player.getColor();
                break;
            }
        }
        if (color == null) {
            throw new CatanException("Cannot make moves in a game you aren't playing!", Status.FORBIDDEN);
        }
        if (outOfTurnApi == null) {
            if (color == state.getCurrentMove().getColor()) {
                return color;
            } else {
                throw new CatanException("Cannot play this move out of turn", Status.FORBIDDEN);
            }
        }
        switch (outOfTurnApi) {
            case THIEF:
                var thieved = state.getCurrentMove().getThieved();
                if (thieved != null && thieved.contains(color)) {
                    return color;
                }
                break;
            case TRADE:
                return color;
            default:
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

    /**
     * Transfer {@param count} of {@param resource} from color {@param from} to color {@param to}
     * If either of the transfer participants is null, it is replaced with the bank
     * If color does not have enough resources, it will error
     * If bank does not have enough resources, bank will provide only as much is available
     * Negative count indicates reverse transfer
     */
    public void transferResources(State state, Color from, Color to, Resource resource, int count)
        throws CatanException {
        // No transfer
        if (from == to || resource == null || count == 0) {
            return;
        }
        // Inverse transfer
        if (count < 0) {
            count = -count;
            var c = from;
            from = to;
            to = c;
        }
        // Get required resource maps
        var fromResources = from == null ? state.getBank() : state.getHand(from).getResources();
        var toResources = to == null ? state.getBank() : state.getHand(to).getResources();
        // Ensure available resources to transfer
        if (from == null) {
            count = Math.min(count, fromResources.get(resource));
        } else if (fromResources.get(resource) < count) {
            throw new BadRequestException("Not enough [" + resource + "] to perform this move");
        }
        // Perform transfer
        Util.addToFrequencyMap(fromResources, resource, -count);
        Util.addToFrequencyMap(toResources, resource, count);
    }

    public void transferResources(State state, Color from, Color to, Resource... resources) throws CatanException {
        for (Resource resource : resources) {
            transferResources(state, from, to, resource, 1);
        }
    }

    public int rollDice() {
        return random.nextInt(MAX_ROLL_PER_DIE) + MIN_ROLL_PER_DIE;
    }

    /**
     * Get all tiles from board matching roll
     */
    public int[] findTileHexesForRoll(Board board, int roll) {
        int tile1 = -1;
        int tile2 = -1;
        Tile[] tiles = board.getTiles();
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i].getRoll() == roll) {
                tile2 = tile1;
                tile1 = i;
            }
        }
        return tile1 == -1 ? EMPTY_INT_ARRAY : tile2 == -1 ? new int[]{tile1} : new int[]{tile1, tile2};
    }

    /**
     * Choose a random resource card from {@param color}'s hand to be stolen
     */
    public Resource chooseRandomlyStolenCard(State state, Color color) {
        Hand hand = state.getHand(color);
        int count = hand.getHandCount();
        if (count == 0) {
            return null;
        }
        int theChosenOne = random.nextInt(count);
        var resources = hand.getResources();
        for (Resource resource : Resource.values()) {
            theChosenOne -= resources.get(resource);
            if (theChosenOne < 0) {
                return resource;
            }
        }
        return null; // Will never happen
    }
}
