/*
 * @author the-codinator
 * created on 2020/6/10
 */

package org.codi.catan.impl.user;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.codi.catan.core.CatanException;
import org.codi.catan.impl.data.CatanDataConnector;
import org.codi.catan.model.game.UserGame;

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
    public List<UserGame> games(String userId, Boolean ongoing) throws CatanException {
        // Handle param "ongoing" - true = only ongoing games, false = only completed games, null = both
        return dataConnector.getUserGamesByUser(userId, ongoing);
    }

}
