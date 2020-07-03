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
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.BadRequestException;
import org.codi.catan.core.CatanException;
import org.codi.catan.impl.data.CatanDataConnector;
import org.codi.catan.impl.user.UserApiHelper;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.Player;
import org.codi.catan.model.game.Ports;
import org.codi.catan.model.game.Resource;
import org.codi.catan.model.game.Tile;
import org.codi.catan.model.request.BoardRequest;
import org.codi.catan.model.user.User;
import org.codi.catan.util.Util;

@Singleton
public class BoardHelper {

    private final GraphHelper graphHelper;
    private final UserApiHelper userApiHelper;
    private final CatanDataConnector dataConnector;
    private final Cache<String, Boolean> validUsers;
    private final Cache<String, Boolean> invalidUsers;

    @Inject
    public BoardHelper(GraphHelper graphHelper, UserApiHelper userApiHelper, CatanDataConnector dataConnector) {
        this.graphHelper = graphHelper;
        this.userApiHelper = userApiHelper;
        this.dataConnector = dataConnector;
        validUsers = CacheBuilder.newBuilder().maximumSize(200).expireAfterWrite(1, TimeUnit.DAYS).build();
        invalidUsers = CacheBuilder.newBuilder().maximumSize(500).expireAfterWrite(1, TimeUnit.HOURS).build();
    }

    /**
     * Normalize and validate created {@param board}, ensuring {@param author} is also a player
     */
    public Board createBoard(BoardRequest request, String author) throws CatanException {
        Board board = normalizeAndValidateBoard(request);
        boolean hasAuthor = false;
        for (Player player : board.getPlayers()) {
            if (player.getId().equals(author)) {
                hasAuthor = true;
                break;
            }
        }
        if (!hasAuthor) {
            throw new BadRequestException("Board creator is not part of game");
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

    private Board normalizeAndValidateBoard(BoardRequest request) throws CatanException {
        Util.validateInput(request);
        Board board = new Board(Util.generateRandomUuid(), request);
        normalizeAndValidateTiles(board.getTiles());
        normalizeAndValidatePorts(board.getPorts());
        validatePlayers(board.getPlayers());
        return board;
    }

    private void normalizeAndValidateTiles(Tile[] tiles) throws CatanException {
        Util.validateInput(tiles, "tiles");
        for (Tile tile : tiles) {
            Util.validateInput(tile, "tile");
        }
        int[] diceRollCounts = new int[DICE_COUNT * MAX_ROLL_PER_DIE + 1];
        for (Resource resource : Resource.values()) {
            int count = 0;
            for (Tile tile : tiles) {
                if (tile.getResource() == resource) {
                    count++;
                    if (tile.getRoll() < DICE_COUNT * MIN_ROLL_PER_DIE
                        || tile.getRoll() > DICE_COUNT * MAX_ROLL_PER_DIE) { // Valid dice rolls
                        throw new BadRequestException("Invalid dice role value for tile");
                    }
                    diceRollCounts[tile.getRoll()]++;
                }
            }
            if (count != resource.getTileCount()) {
                throw new BadRequestException("Invalid number of tiles for " + resource.toString());
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
            throw new BadRequestException("Invalid number of desert tiles");
        }
        if (!Arrays.equals(graphHelper.getDiceRollCount(), diceRollCounts)) {
            throw new BadRequestException("Invalid dice roll distribution on tiles");
        }
    }

    private void normalizeAndValidatePorts(Ports ports) throws CatanException {
        Util.validateInput(ports);
        EnumMap<Resource, Integer> ports21 = ports.getPorts21();
        Set<Integer> ports31 = ports.getPorts31();
        Util.validateInput(ports21, "2:1 ports");
        Util.validateInput(ports31, "3:1 ports");
        if (ports21.size() != Resource.values().length) {
            throw new BadRequestException("Incorrect 2:1 resource port definition");
        }
        if (ports31.size() != graphHelper.getPortCount() - Resource.values().length) {
            throw new BadRequestException("Incorrect 3:1 port definition");
        }
        Set<Integer> normalizedPorts31 = new TreeSet<>(); // TreeSet to maintain order during initial creation
        for (int vertex : ports31) {
            normalizedPorts31.add(graphHelper.normalizeAndValidatePort(vertex));
        }
        ports.setPorts31(normalizedPorts31);
        for (Resource resource : Resource.values()) {
            int vertex = ports21.get(resource);
            int normalizedVertex = graphHelper.normalizeAndValidatePort(vertex);
            if (normalizedPorts31.contains(normalizedVertex)) {
                throw new BadRequestException("Duplicate port vertex in 2:1 and 3:1");
            }
            if (vertex != normalizedVertex) {
                ports21.put(resource, vertex);
            }
        }
    }

    private void validatePlayers(Player[] players) throws CatanException {
        Util.validateInput(players, "players");
        boolean[] colors = new boolean[Color.values().length];
        Set<String> users = new HashSet<>(colors.length);
        for (Player player : players) {
            if (player.getColor() == null || colors[player.getColor().ordinal()]) {
                throw new BadRequestException("Missing / duplicate player color");
            }
            colors[player.getColor().ordinal()] = true;
            userApiHelper.validateUserId(player.getId());
            users.add(player.getId());
        }
        for (int i = 0; i < colors.length; i++) {
            if (!colors[i]) {
                throw new BadRequestException("Missing player color - " + Color.values()[i]);
            }
        }
        if (users.size() != colors.length) {
            throw new BadRequestException("Duplicate user");
        }
        var invalids = invalidUsers.getAllPresent(users);
        if (!invalids.isEmpty()) {
            throw new BadRequestException("Invalid Users - " + invalids.keySet());
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
                    throw new BadRequestException("Invalid Users - [" + unknownUserId + "]");
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
                throw new BadRequestException("Invalid Users - " + users.toString());
            }
        }
    }
}
