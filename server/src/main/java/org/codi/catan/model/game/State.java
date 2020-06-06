/*
 * @author the-codinator
 * created on 2020/5/26
 */

package org.codi.catan.model.game;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codi.catan.core.CatanException;

@NoArgsConstructor
@Getter
@Setter
public class State extends BaseState {

    private List<DevCard> bankDevCards;
    @JsonInclude(Include.NON_NULL)
    private EnumMap<Color, Hand> hands;

    /**
     * Create the default state for a new board with starting player {@param color} and {@param thief} tile position
     * Initialize everything so that we don't need to deal with nulls (except inside maps)
     */
    public State(String id, Color color, int thief) throws CatanException {
        super(id, Phase.setup1, Collections.emptyMap(), Collections.emptyList(), thief, Resource.createNewBank(),
            new EnumMap<>(Color.class), new EnumMap<>(AchievementType.class), new CurrentMove(color), null);
        this.bankDevCards = DevCard.createRandomInitial();
        this.hands = new EnumMap<>(Color.class);
        for (AchievementType type : AchievementType.values()) {
            this.getAchievements().put(type, new AchievementValue(type.getThreshold()));
        }
    }

    public Hand getHand(Color color) {
        return color == null ? null
            : hands.computeIfAbsent(color, __ -> new Hand(new EnumMap<>(Resource.class), new LinkedList<>()));
    }
}
