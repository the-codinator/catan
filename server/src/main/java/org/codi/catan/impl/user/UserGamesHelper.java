/*
 * @author the-codinator
 * created on 2020/6/10
 */

package org.codi.catan.impl.user;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.impl.data.CatanDataConnector;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.Player;
import org.codi.catan.model.user.Games;

@Singleton
public class UserGamesHelper {

    private final CatanDataConnector dataConnector;

    @Inject
    public UserGamesHelper(CatanDataConnector dataConnector) {
        this.dataConnector = dataConnector;
    }

    /**
     * Get users ongoing and recently completed games
     */
    public Games games(String userId) throws CatanException {
        return dataConnector.getGames(userId);
    }

    /**
     * Handle new game for players
     */
    public void handleNewGame(Board board) throws CatanException {
        List<Games> gamesList = new ArrayList<>(4); // 4 players per game
        List<String> users = new ArrayList<>(4);
        for (Player player : board.getPlayers()) {
            try {
                String user = player.getId();
                users.add(user);
                gamesList.add(dataConnector.getGames(user));
            } catch (CatanException e) {
                if (e.getErrorStatus() == Status.NOT_FOUND) {
                    Games games = new Games(player.getId());
                    gamesList.add(games);
                } else {
                    throw e;
                }
            }
        }
        for (Games games : gamesList) {
            games.addNewGame(board.getId(), users);
        }
        for (Games games : gamesList) {
            dataConnector.putGames(games);
        }
    }

    /**
     * Handle completed game for players
     */
    public void handleCompletedGame(Board board) throws CatanException {
        handleCompletedGameInternal(board, false);
    }

    public void handleDeletedGame(Board board) throws CatanException {
        handleCompletedGameInternal(board, true);
    }

    private void handleCompletedGameInternal(Board board, boolean isDelete) throws CatanException {
        List<Games> gamesList = new ArrayList<>(4); // 4 players per game
        for (Player player : board.getPlayers()) {
            try {
                gamesList.add(dataConnector.getGames(player.getId()));
            } catch (CatanException e) {
                if (e.getErrorStatus() != Status.NOT_FOUND) {
                    throw e;
                }
            }
        }
        for (Games games : gamesList) {
            if (isDelete) {
                games.getOngoing().removeIf(game -> game.getId().equals(board.getId()));
                games.getCompleted().removeIf(game -> game.getId().equals(board.getId()));
            } else {
                games.completeGame(board.getId());
            }
        }
        for (Games games : gamesList) {
            dataConnector.putGames(games);
        }
    }
}
