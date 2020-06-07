/*
 * @author the-codinator
 * created on 2020/5/28
 */

package org.codi.catan.model.game;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class CurrentMove {

    private Color color;
    private Roll roll;
    private DevCard devCard;
    private List<Trade> trades;
    private EnumSet<Color> thieved; // Colors who drop cards coz thief & 8+ cards

    public CurrentMove(Color color) {
        this.color = color;
        this.trades = new LinkedList<>();
    }
}
