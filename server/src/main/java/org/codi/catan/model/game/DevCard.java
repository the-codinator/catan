/*
 * @author the-codinator
 * created on 2020/5/28
 */

package org.codi.catan.model.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.request.DevCardRequest;

@AllArgsConstructor
public enum DevCard {

    knight(14),
    monopoly(2),
    road_building(2),
    year_of_plenty(2),
    // Victory Points
    chapel,
    great_hall,
    library,
    market,
    university;

    DevCard() {
        this(1);
    }

    private static final int TOTAL_COUNT = 25;
    private final int count;

    /**
     * Create a random list of dev cards for the bank of a new game
     */
    public static List<DevCard> createRandomInitial() throws CatanException {
        List<DevCard> list = new ArrayList<>(TOTAL_COUNT);
        for (DevCard dc : values()) {
            for (int i = dc.count; i > 0; i--) {
                list.add(dc);
            }
        }
        if (list.size() != TOTAL_COUNT) {
            throw new CatanException("Incorrect number of Dev Cards in the pack");
        }
        Collections.shuffle(list);
        return list;
    }

    /**
     * Play a dev card and mutate the game's state as per its rules
     */
    public static void playCard(DevCardRequest input, State state) { // TODO:
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
