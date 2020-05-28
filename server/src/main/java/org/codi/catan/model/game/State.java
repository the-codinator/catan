/*
 * @author the-codinator
 * created on 2020/5/26
 */

package org.codi.catan.model.game;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.EnumMap;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class State extends BaseState {

    private List<DevCard> bankDevCards;
    @JsonInclude(Include.NON_NULL)
    private EnumMap<Color, Hand> hands;

    public State(String id) {
        super(id);
    }

    public Hand getHand(Color color) {
        return color == null || hands == null ? null : hands.get(color);
    }
}
