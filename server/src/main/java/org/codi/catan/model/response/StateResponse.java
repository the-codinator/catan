/*
 * @author the-codinator
 * created on 2020/5/28
 */

package org.codi.catan.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.EnumMap;
import lombok.Getter;
import lombok.Setter;
import org.codi.catan.model.game.BaseState;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.Hand;
import org.codi.catan.model.game.State;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class StateResponse extends BaseState {

    /**
     * Create State Response object from {@param state} for {@param color}
     */
    public StateResponse(State state, Color color) {
        super(state.getId(), state.getPhase(), state.getHouses(), state.getRoads(), state.getThief(), state.getBank(),
            state.getPlayedDevCards(), state.getAchievements(), state.getCurrentMove(), state.getETag());
        this.otherHandCounts = new EnumMap<>(Color.class);
        for (Color c : Color.values()) {
            Hand hand = state.getHand(color);
            if (c == color) {
                this.hand = hand;
            } else {
                otherHandCounts.put(c, hand.getHandCount());
            }
        }
    }

    private Hand hand;
    private EnumMap<Color, Integer> otherHandCounts;
}
