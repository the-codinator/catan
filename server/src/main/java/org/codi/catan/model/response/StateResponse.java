/*
 * @author the-codinator
 * created on 2020/5/28
 */

package org.codi.catan.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codi.catan.model.game.BaseState;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.Hand;
import org.codi.catan.model.game.State;

@NoArgsConstructor
@Getter
@Setter
public class StateResponse extends BaseState {

    public StateResponse(State state) {
        this(state, state.getCurrentMove().getColor());
    }

    public StateResponse(State state, Color color) {
        super(state.getId(), state.getPhase(), state.getHouses(), state.getRoads(), state.getThief(), state.getBank(),
            state.getPlayedDevCards(), state.getAchievements(), state.getCurrentMove(), state.getETag());
        Hand hand = state.getHand(color);
        this.hand = hand == null ? new Hand() : hand;
    }

    private Hand hand;
}
