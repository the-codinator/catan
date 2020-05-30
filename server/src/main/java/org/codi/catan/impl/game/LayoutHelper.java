/*
 * @author the-codinator
 * created on 2020/5/26
 */

package org.codi.catan.impl.game;

import static org.codi.catan.util.Constants.DICE_COUNT;
import static org.codi.catan.util.Constants.MAX_ROLL_PER_DIE;
import static org.codi.catan.util.Constants.MIN_ROLL_PER_DIE;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.impl.data.CatanDataConnector;
import org.codi.catan.impl.user.UserApiHelper;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.Player;
import org.codi.catan.model.game.Port;
import org.codi.catan.model.game.Resource;
import org.codi.catan.model.game.Tile;
import org.codi.catan.model.user.User;
import org.codi.catan.util.Util;

@Singleton
public class LayoutHelper {

    private final GraphHelper graphHelper;
    private final UserApiHelper userApiHelper;
    private final CatanDataConnector dataConnector;
    private final Cache<String, Boolean> validUsers;
    private final Cache<String, Boolean> invalidUsers;

    @Inject
    public LayoutHelper(GraphHelper graphHelper, UserApiHelper userApiHelper, CatanDataConnector dataConnector) {
        this.graphHelper = graphHelper;
        this.userApiHelper = userApiHelper;
        this.dataConnector = dataConnector;
        validUsers = CacheBuilder.newBuilder().maximumSize(200).expireAfterWrite(1, TimeUnit.DAYS).build();
        invalidUsers = CacheBuilder.newBuilder().maximumSize(500).expireAfterWrite(1, TimeUnit.HOURS).build();
    }

    /**
     * Normalize and validate created {@param board}, ensuring {@param author} is also a player
     */
    public Board createBoard(Board board, String author) throws CatanException {
        normalizeAndValidateBoard(board);
        boolean hasAuthor = false;
        for (Player player : board.getPlayers()) {
            if (player.getId().equals(author)) {
                hasAuthor = true;
                break;
            }
        }
        if (!hasAuthor) {
            throw new CatanException("Board creator is not part of game", Status.BAD_REQUEST);
        }
        try {
            dataConnector.createBoard(board);
        } catch (CatanException e) {
            throw new CatanException("Error creating game", Status.INTERNAL_SERVER_ERROR, e);
        }
        return board;
    }

    /**
     * Get board with {@param gameId}
     */
    public Board getBoard(String gameId) throws CatanException {
        try {
            return dataConnector.getBoard(gameId);
        } catch (CatanException e) {
            if (e.getErrorStatus() == Status.NOT_FOUND) {
                throw new CatanException("Could not find board with id - " + gameId, Status.NOT_FOUND, e);
            } else {
                throw e;
            }
        }
    }

    private void normalizeAndValidateBoard(Board board) throws CatanException {
        Util.validateInput(board);
        board.setId(Util.generateRandomUuid().toString());
        normalizeAndValidateTiles(board.getTiles());
        normalizeAndValidatePorts(board.getPorts());
        validatePlayers(board.getPlayers());
    }

    private void normalizeAndValidateTiles(Tile[] tiles) throws CatanException {
        Util.validateInput(tiles);
        for (Tile tile : tiles) {
            Util.validateInput(tile);
        }
        int[] diceRollCounts = new int[DICE_COUNT * MAX_ROLL_PER_DIE + 1];
        for (Resource resource : Resource.values()) {
            int count = 0;
            for (Tile tile : tiles) {
                if (tile.getResource() == resource) {
                    count++;
                    if (tile.getRoll() < DICE_COUNT * MIN_ROLL_PER_DIE
                        || tile.getRoll() > DICE_COUNT * MAX_ROLL_PER_DIE) { // Valid dice rolls
                        throw new CatanException("Invalid dice role value for tile", Status.BAD_REQUEST);
                    }
                    diceRollCounts[tile.getRoll()]++;
                }
            }
            if (count != resource.getTileCount()) {
                throw new CatanException("Invalid number of tiles for " + resource.toString(), Status.BAD_REQUEST);
            }
        }
        int count = 0;
        for (Tile tile : tiles) {
            if (tile.getResource() == null) {
                count++;
                tile.setRoll(0);
                diceRollCounts[0]++;
            }
        }
        if (count != 1) { // Desert
            throw new CatanException("Invalid number of desert tiles", Status.BAD_REQUEST);
        }
        if (!Arrays.equals(graphHelper.getDiceRollCount(), diceRollCounts)) {
            throw new CatanException("Invalid dice roll distribution on tiles", Status.BAD_REQUEST);
        }
    }

    private void normalizeAndValidatePorts(Port[] ports) throws CatanException {
        Util.validateInput(ports);
        boolean[] portResource = new boolean[Resource.values().length];
        for (Port port : ports) {
            Util.validateInput(port);
            port.setVertex(graphHelper.normalizeAndValidatePort(port.getVertex()));
            if (port.getResource() != null) {
                if (portResource[port.getResource().ordinal()]) {
                    throw new CatanException("Port with duplicate resource", Status.BAD_REQUEST);
                } else {
                    portResource[port.getResource().ordinal()] = true;
                }
            }
        }
        for (int i = 0; i < portResource.length; i++) {
            if (!portResource[i]) {
                throw new CatanException("Resource port missing - " + Resource.values()[i], Status.BAD_REQUEST);
            }
        }
        Arrays.sort(ports, Comparator.comparingInt(Port::getVertex));
        if (ports[0].getVertex() < 0) {
            throw new CatanException(
                "Invalid port vertices, allowed=" + Arrays.toString(graphHelper.getPortVertexList()),
                Status.BAD_REQUEST);
        }
        if (!Arrays.equals(Arrays.stream(ports).mapToInt(Port::getVertex).toArray(), graphHelper.getPortVertexList())) {
            throw new CatanException("Invalid number of 3:1 ports", Status.BAD_REQUEST);
        }
    }

    private void validatePlayers(Player[] players) throws CatanException {
        Util.validateInput(players);
        boolean[] colors = new boolean[Color.values().length];
        Set<String> users = new HashSet<>(colors.length);
        for (Player player : players) {
            if (player.getColor() == null || colors[player.getColor().ordinal()]) {
                throw new CatanException("Missing / duplicate player color", Status.BAD_REQUEST);
            }
            colors[player.getColor().ordinal()] = true;
            userApiHelper.validateUserId(player.getId());
            users.add(player.getId());
        }
        for (int i = 0; i < colors.length; i++) {
            if (!colors[i]) {
                throw new CatanException("Missing player color - " + Color.values()[i], Status.BAD_REQUEST);
            }
        }
        if (users.size() != colors.length) {
            throw new CatanException("Duplicate user", Status.BAD_REQUEST);
        }
        var invalids = invalidUsers.getAllPresent(users);
        if (!invalids.isEmpty()) {
            throw new CatanException("Invalid Users - " + invalids.keySet(), Status.BAD_REQUEST);
        }
        users.removeAll(validUsers.getAllPresent(users).keySet());
        if (users.isEmpty()) {
            return;
        }
        if (users.size() == 1) {
            String unknownUserId = users.iterator().next();
            try {
                dataConnector.getUser(unknownUserId);
                validUsers.put(unknownUserId, Boolean.TRUE);
            } catch (CatanException e) {
                if (e.getErrorStatus() == Status.NOT_FOUND) {
                    invalidUsers.put(unknownUserId, Boolean.FALSE);
                    throw new CatanException("Invalid Users - [" + unknownUserId + "]", Status.BAD_REQUEST);
                } else {
                    throw e;
                }
            }
        } else {
            User[] unknownUsers = dataConnector.getUsers(users.toArray(new String[0]));
            for (User u : unknownUsers) {
                validUsers.put(u.getId(), Boolean.TRUE);
            }
            if (unknownUsers.length != users.size()) {
                for (User u : unknownUsers) {
                    users.remove(u.getId());
                }
                for (String u : users) {
                    invalidUsers.put(u, Boolean.FALSE);
                }
                throw new CatanException("Invalid Users - " + users.toString(), Status.BAD_REQUEST);
            }
        }
    }
}
