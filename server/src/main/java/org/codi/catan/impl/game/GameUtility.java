/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.impl.game;

import static org.codi.catan.util.Constants.EMPTY_INT_ARRAY;
import static org.codi.catan.util.Constants.MAX_ROLL_PER_DIE;
import static org.codi.catan.util.Constants.MIN_ROLL_PER_DIE;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.BadRequestException;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.Hand;
import org.codi.catan.model.game.House;
import org.codi.catan.model.game.OutOfTurnApi;
import org.codi.catan.model.game.Phase;
import org.codi.catan.model.game.Player;
import org.codi.catan.model.game.Resource;
import org.codi.catan.model.game.State;
import org.codi.catan.model.game.Tile;
import org.codi.catan.util.Util;

@Singleton
public class GameUtility {

    private final GraphHelper graphHelper;
    private final Random random = new Random();

    @Inject
    public GameUtility(GraphHelper graphHelper) {
        this.graphHelper = graphHelper;
    }

    /**
     * Validates that the current turn belongs specified {@param user}
     *
     * @return Color of the player making the request
     */
    public Color checkPlayerTurn(Board board, State state, String user, OutOfTurnApi outOfTurnApi)
        throws CatanException {
        if (outOfTurnApi == OutOfTurnApi.ADMIN) {
            return state.getCurrentMove().getColor();
        }
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
     * Find the desert (and initial thief position) on the {@param board}
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
     *
     * @return Actual number of resources transferred
     */
    public int transferResources(State state, Color from, Color to, Resource resource, int count)
        throws CatanException {
        // No transfer
        if (from == to || resource == null || count == 0) {
            return 0;
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
            count = Math.min(count, fromResources.getOrDefault(resource, 0));
        } else if (fromResources.getOrDefault(resource, 0) < count) {
            throw new BadRequestException("Not enough [" + resource + "] to perform this move");
        }
        // Perform transfer
        Util.addToFrequencyMap(fromResources, resource, -count);
        Util.addToFrequencyMap(toResources, resource, count);
        // Return actual transfer amount
        return count;
    }

    public int transferResources(State state, Color from, Color to, Resource... resources) throws CatanException {
        int count = 0;
        for (Resource resource : resources) {
            count += transferResources(state, from, to, resource, 1);
        }
        return count;
    }

    public int transferResources(State state, Color from, Color to, Map<Resource, Integer> resources)
        throws CatanException {
        int count = 0;
        for (var entry : resources.entrySet()) {
            count += transferResources(state, from, to, entry.getKey(), entry.getValue());
        }
        return count;
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
        int count = hand.getResourceCount();
        if (count == 0) {
            return null;
        }
        int theChosenOne = random.nextInt(count);
        var resources = hand.getResources();
        for (Resource resource : Resource.values()) {
            theChosenOne -= resources.getOrDefault(resource, 0);
            if (theChosenOne < 0) {
                return resource;
            }
        }
        return null; // Will never happen
    }

    /**
     * Check if current player has a house on 2:1 {@param resource} port or 3:1 (resource = null) port
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasHouseOnPort(Board board, State state, Resource resource) {
        Color color = state.getCurrentMove().getColor();
        Set<Integer> ports;
        if (resource != null) {
            ports = Set.of(board.getPorts().getPorts21().get(resource));
        } else {
            ports = board.getPorts().getPorts31();
        }
        for (int port : ports) {
            House house = state.getHouses().get(port);
            if (house == null) {
                house = state.getHouses().get(graphHelper.getComplementaryPortVertex(port));
            }
            if (house != null && house.getColor() == color) {
                return true;
            }
        }
        return false;
    }
}
