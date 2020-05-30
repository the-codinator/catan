/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.impl.game;

import javax.inject.Singleton;
import org.codi.catan.model.game.DevCard;
import org.codi.catan.model.game.State;
import org.codi.catan.model.request.DevCardRequest;

@Singleton
public class DevCardMoveHelper { // TODO:

    /**
     * Play a dev card and mutate the game's state as per its rules
     */
    public void playCard(DevCardRequest input, State state) { // TODO:
        DevCard devCard = input.getDevCard();
        // Update state based on dev card's rules. player from state.getCurrentTurn. check state.currentMove.devCard
        switch (devCard) {
            case knight:
                break;
            case road_building:
                break;
            case year_of_plenty:
                break;
            case monopoly:
                break;
            default:
                // Victory cards cannot be played! They are revealed on game end...
        }
    }
}
