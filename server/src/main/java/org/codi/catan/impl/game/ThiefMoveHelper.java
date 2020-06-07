/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.impl.game;

import static org.codi.catan.util.Constants.DROP_CARDS_FOR_THIEF_THRESHOLD;

import java.util.EnumSet;
import javax.inject.Singleton;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.State;

@Singleton
public class ThiefMoveHelper { // TODO:

    /**
     * Handle when someone rolls a 7 on the dice
     */
    public void handleThiefRoll(State state) { // TODO: Enable once thief is implemented
        // state.setPhase(Phase.thief);
        EnumSet<Color> thieved = EnumSet.noneOf(Color.class);
        for (var colorHand : state.getHands().entrySet()) {
            if (colorHand.getValue().getHandCount() > DROP_CARDS_FOR_THIEF_THRESHOLD) {
                thieved.add(colorHand.getKey());
            }
        }
        if (!thieved.isEmpty()) {
            state.getCurrentMove().setThieved(thieved);
        }
    }
}
