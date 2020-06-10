/*
 * @author the-codinator
 * created on 2020/6/7
 */

package org.codi.catan.impl.game;

import javax.inject.Singleton;
import org.codi.catan.model.game.AchievementType;
import org.codi.catan.model.game.AchievementValue;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.DevCard;
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

    /**
     * Check for any changes to the largest army achievement
     */
    public void handleLargestArmy(State state) {
        Color color = state.getCurrentMove().getColor();
        AchievementValue achievement = state.getAchievements().get(AchievementType.largest_army);
        if (achievement.getColor() == color) {
            return;
        }
        int knights = (int) state.getPlayedDevCards().get(color).stream().filter(DevCard.knight::equals).count();
        if (knights > achievement.getCount()) {
            achievement.setColor(color);
            achievement.setCount(knights);
        }
    }
}
