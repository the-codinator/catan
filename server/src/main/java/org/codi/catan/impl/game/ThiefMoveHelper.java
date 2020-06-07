/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.impl.game;

import static org.codi.catan.util.Constants.DROP_CARDS_FOR_THIEF_THRESHOLD;

import java.util.EnumSet;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.codi.catan.core.BadRequestException;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.Hand;
import org.codi.catan.model.game.House;
import org.codi.catan.model.game.Phase;
import org.codi.catan.model.game.Resource;
import org.codi.catan.model.game.State;
import org.codi.catan.model.request.ThiefDropRequest;
import org.codi.catan.model.request.ThiefPlayRequest;
import org.codi.catan.util.Util;

@Singleton
public class ThiefMoveHelper {

    private final GraphHelper graphHelper;
    private final GameUtility gameUtility;

    @Inject
    public ThiefMoveHelper(GraphHelper graphHelper, GameUtility gameUtility) {
        this.graphHelper = graphHelper;
        this.gameUtility = gameUtility;
    }

    /**
     * Handle when someone rolls a 7 on the dice
     */
    public void handleThiefRoll(State state) {
        state.setPhase(Phase.thief);
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

    /**
     * Drop half your resource cards when dice roll 7 and have more than 7 cards
     */
    public void thiefDrop(State state, Color color, ThiefDropRequest request) throws CatanException {
        Hand hand = state.getHand(color);
        Util.validateInput(request.getResources());
        if (request.getResources().length != hand.getHandCount()) {
            throw new BadRequestException("Incorrect number of resource cards - need " + hand.getHandCount() / 2);
        }
        gameUtility.transferResources(state, color, null, request.getResources());
        state.getCurrentMove().getThieved().remove(color);
        if (state.getCurrentMove().getThieved().isEmpty()) {
            state.getCurrentMove().setThieved(null);
        }
    }

    /**
     * Move thief to a different tile and steal a card from another player (if possible)
     */
    public void thiefPlay(State state, ThiefPlayRequest request) throws CatanException {
        if (state.getCurrentMove().getThieved() != null && !state.getCurrentMove().getThieved().isEmpty()) {
            throw new BadRequestException(
                "Please wait until players with 8+ cards have dropped half - " + state.getCurrentMove().getThieved());
        }
        if (request.getHex() == state.getThief()) {
            throw new BadRequestException("Thief MUST be moved to a different tile");
        }
        Color color = state.getCurrentMove().getColor();
        if (color == request.getColor()) {
            throw new BadRequestException("Cannot steal from self");
        }
        graphHelper.validateHex(request.getHex());
        int[] vertices = graphHelper.getVerticesAroundHex(request.getHex());
        boolean hasHouse = false;
        for (int vertex : vertices) {
            House house = state.getHouses().get(vertex);
            if (house != null && house.getColor() != color) {
                if (request.getColor() == null && state.getHand(house.getColor()).getHandCount() > 0) {
                    throw new BadRequestException("Must steal from a player if possible");
                }
                if (house.getColor() == request.getColor()) {
                    hasHouse = true;
                    break;
                }
            }
        }
        if (request.getColor() == null) {
            return;
        }
        if (!hasHouse) {
            throw new BadRequestException("Cannot steal from player without house on thief tile");
        }
        Resource resource = gameUtility.chooseRandomlyStolenCard(state, request.getColor());
        // Note: resource can be null if chosen player has no cards
        gameUtility.transferResources(state, request.getColor(), color, resource);
    }
}
