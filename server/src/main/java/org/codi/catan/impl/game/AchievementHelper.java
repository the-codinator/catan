/*
 * @author the-codinator
 * created on 2020/6/7
 */

package org.codi.catan.impl.game;

import javax.inject.Singleton;
import org.codi.catan.model.game.Road;
import org.codi.catan.model.game.State;

@Singleton
public class AchievementHelper {

    /**
     * Check for any changes to the longest road achievement
     *
     * @param road the seed road which MUST be part of the chain
     */
    public void handleLongestRoad(State state, Road road) { // TODO: apply BFS

    }

    public void handleLargestArmy(State state) {
        // TODO:
    }
}
