/*
 * @author the-codinator
 * created on 2020/8/8
 */

package org.codi.catan.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.core.IdentifiableEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserGame implements IdentifiableEntity {

    private String id;
    private String game;
    private String user;
    private Color color;
    private boolean myTurn;
    private boolean completed;

    public UserGame(Board board, Color color) throws CatanException {
        this(board, getPlayer(board, color), false);
    }

    private UserGame(Board board, Player player, boolean completed) {
        this(generateId(board.getId(), player.getId()), board.getId(), player.getId(), player.getColor(), false,
            completed);
    }

    public static String generateId(String game, String user) {
        return game + ':' + user;
    }

    /**
     * Create UserGame object for each player in the game
     */
    public static UserGame[] createUserGamesFromBoard(Board board) {
        boolean completed = board.getCompleted() != 0;
        UserGame[] ugs = new UserGame[board.getPlayers().length];
        for (int i = 0; i < ugs.length; i++) {
            ugs[i] = new UserGame(board, board.getPlayers()[i], completed);
        }
        if (!completed) {
            ugs[0].setMyTurn(true);
        }
        return ugs;
    }

    private static Player getPlayer(Board board, Color color) throws CatanException {
        Player[] players = board.getPlayers();
        for (Player player : players) {
            if (player.getColor() == color) {
                return player;
            }
        }
        throw new CatanException("Got null color for UserGame");
    }
}
