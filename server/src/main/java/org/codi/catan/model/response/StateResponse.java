/*
 * @author the-codinator
 * created on 2020/5/28
 */

package org.codi.catan.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
@JsonInclude(Include.NON_NULL)
public class StateResponse extends BaseState {

    public StateResponse(State state, Color color) {
        super(state.getId(), state.getPhase(), state.getHouses(), state.getRoads(), state.getThief(), state.getBank(),
            state.getPlayedDevCards(), state.getAchievements(), state.getCurrentMove(), state.getETag());
        this.hand = state.getHand(color);
    }

    private Hand hand;
}
