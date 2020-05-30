/*
 * @author the-codinator
 * created on 2020/5/28
 */

package org.codi.catan.model.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.codi.catan.core.CatanException;

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

    DevCard(int count) {
        this(count, false);
    }

    DevCard() {
        this(1, true);
    }

    private static final int TOTAL_COUNT = 25;
    private final int count;
    @Getter
    private final boolean victoryPoint;

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
}
