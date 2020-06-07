/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.impl.game;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.DevCard;
import org.codi.catan.model.game.Resource;
import org.codi.catan.model.game.State;
import org.codi.catan.model.request.DevCardRequest;

@Singleton
public class DevCardMoveHelper { // TODO:

    private final GameUtility gameUtility;

    @Inject
    public DevCardMoveHelper(GameUtility gameUtility) {
        this.gameUtility = gameUtility;
    }

    /**
     * Buy a Development Card
     */
    public void buy(State state) throws CatanException {
        if (state.getBankDevCards().isEmpty()) {
            throw new CatanException("No Dev Cards available in Bank", Status.BAD_REQUEST);
        }
        Color color = state.getCurrentMove().getColor();
        gameUtility.transferResources(state, color, null, Resource.hay, Resource.sheep, Resource.rock);
        state.getHand(color).getDevCards().add(state.getBankDevCards().remove(0));
    }

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
