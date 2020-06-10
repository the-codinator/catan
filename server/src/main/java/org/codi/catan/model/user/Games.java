/*
 * @author the-codinator
 * created on 2020/6/10
 */

package org.codi.catan.model.user;

import static org.codi.catan.util.Constants.MAX_ONGOING_GAMES_PER_USER;
import static org.codi.catan.util.Constants.MAX_RECENTLY_COMPLETED_GAMES_PER_USER;

import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.core.Response.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.core.IdentifiableEntity;

@NoArgsConstructor
@Getter
@Setter
public class Games implements IdentifiableEntity {

    private String id;
    private LinkedList<Game> ongoing;
    private LinkedList<Game> completed;

    public Games(String id) {
        this.id = id;
        this.ongoing = new LinkedList<>();
        this.completed = new LinkedList<>();
    }

    /**
     * Add a new ongoing game
     */
    public void addNewGame(String id, List<String> players) throws CatanException {
        Game game = new Game(id, players, System.currentTimeMillis(), 0);
        if (ongoing.size() > MAX_ONGOING_GAMES_PER_USER) {
            throw new CatanException("Too many games for [" + id + "], max = " + MAX_ONGOING_GAMES_PER_USER,
                Status.FORBIDDEN);
        }
        ongoing.addFirst(game);
    }

    /**
     * Move a game from ongoing to completed
     */
    public void completeGame(String id) {
        for (Game game : ongoing) {
            if (game.getId().equals(id)) {
                ongoing.remove(game);
                game.setCompleted(System.currentTimeMillis());
                completed.addFirst(game);
                if (completed.size() > MAX_RECENTLY_COMPLETED_GAMES_PER_USER) {
                    completed.removeLast();
                }
                break;
            }
        }
    }
}
