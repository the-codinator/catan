/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.impl.game;

import static org.codi.catan.util.Constants.MAX_ROADS_PER_PLAYER;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.codi.catan.core.BadRequestException;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.DevCard;
import org.codi.catan.model.game.Resource;
import org.codi.catan.model.game.Road;
import org.codi.catan.model.game.State;
import org.codi.catan.model.request.DevCardRequest;
import org.codi.catan.model.request.RoadRequest;
import org.codi.catan.model.request.ThiefPlayRequest;
import org.codi.catan.util.Util;

@Singleton
public class DevCardMoveHelper {

    private final GameUtility gameUtility;
    private final ThiefMoveHelper thiefMoveHelper;
    private final BuildMoveHelper buildMoveHelper;
    private final AchievementHelper achievementHelper;

    @Inject
    public DevCardMoveHelper(GameUtility gameUtility, ThiefMoveHelper thiefMoveHelper, BuildMoveHelper buildMoveHelper,
        AchievementHelper achievementHelper) {
        this.gameUtility = gameUtility;
        this.thiefMoveHelper = thiefMoveHelper;
        this.buildMoveHelper = buildMoveHelper;
        this.achievementHelper = achievementHelper;
    }

    /**
     * Buy a Development Card
     */
    public void buy(State state) throws CatanException {
        if (state.getBankDevCards().isEmpty()) {
            throw new BadRequestException("No Dev Cards available in Bank");
        }
        Color color = state.getCurrentMove().getColor();
        gameUtility.transferResources(state, color, null, Resource.hay, Resource.sheep, Resource.rock);
        state.getHand(color).getDevCards().add(state.getBankDevCards().remove(0));
    }

    /**
     * Play a dev card and mutate the game's state as per its rules
     */
    public void play(State state, DevCardRequest request) throws CatanException {
        if (state.getCurrentMove().getDevCard() != null) {
            throw new BadRequestException("Can play at most 1 dev card per turn");
        }
        DevCard devCard = request.getType();
        Util.validateInput(devCard, "type");
        Color color = state.getCurrentMove().getColor();
        if (!state.getHand(color).getDevCards().remove(devCard)) {
            throw new BadRequestException("Dev Card not present in hand");
        }
        state.getCurrentMove().setDevCard(devCard);
        List<DevCard> playedDevCards = state.getPlayedDevCards().get(color);
        if (playedDevCards == null) {
            state.getPlayedDevCards().put(color, List.of(devCard));
        } else {
            playedDevCards.add(devCard);
        }
        switch (devCard) {
            case knight:
                knight(state, request.getHex(), request.getVictim());
                break;
            case road_building:
                roadBuilding(state, request.getRoad1(), request.getRoad2());
                break;
            case year_of_plenty:
                yearOfPlenty(state, request.getResource1(), request.getResource2());
                break;
            case monopoly:
                monopoly(state, request.getResource());
                break;
            default:
                throw new BadRequestException(
                    "Cannot play Victory cards, they are revealed on game end. If you are winning, end your turn!");
        }
    }

    private void knight(State state, Integer hex, Color victim) throws CatanException {
        Util.validateInput(hex, "hex");
        thiefMoveHelper.thiefPlay(state, new ThiefPlayRequest(hex, victim));
        achievementHelper.handleLargestArmy(state);
    }

    private void roadBuilding(State state, RoadRequest r1, RoadRequest r2) throws CatanException {
        Color color = state.getCurrentMove().getColor();
        int roadCount = Util.count(state.getRoads(), road -> road.getColor().equals(color));
        if (MAX_ROADS_PER_PLAYER - roadCount >= 1) {
            Util.validateInput(r1, "road1");
            Road road1 = new Road(color, r1.getVertex1(), r1.getVertex2());
            buildMoveHelper.ensureCanPlaceRoad(state, road1.getVertex1(), road1.getVertex2());
            state.getRoads().add(road1);
            if (MAX_ROADS_PER_PLAYER - roadCount >= 2) {
                Util.validateInput(r2, "road2");
                Road road2 = new Road(color, r2.getVertex1(), r2.getVertex2());
                buildMoveHelper.ensureCanPlaceRoad(state, road2.getVertex1(), road2.getVertex2());
                state.getRoads().add(road2);
            }
            achievementHelper.handleLongestRoad(state);
        }
    }

    private void yearOfPlenty(State state, Resource r1, Resource r2) throws CatanException {
        int bankCount = Util.getFrequencyMapTotalCount(state.getBank());
        if (bankCount >= 1) {
            Util.validateInput(r1, "resource1");
        } else {
            r1 = null;
        }
        if (bankCount >= 2) {
            Util.validateInput(r2, "resource2");
        } else {
            r2 = null;
        }
        gameUtility.transferResources(state, null, state.getCurrentMove().getColor(), r1, r2);
    }

    private void monopoly(State state, Resource resource) throws CatanException {
        Util.validateInput(resource, "resource");
        Color turn = state.getCurrentMove().getColor();
        for (Color color : Color.values()) {
            if (color != turn) {
                gameUtility.transferResources(state, color, turn, resource,
                    state.getHand(color).getResources().getOrDefault(resource, 0));
            }
        }
    }
}
