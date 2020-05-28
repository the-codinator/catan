/*
 * @author the-codinator
 * created on 2020/5/28
 */

package org.codi.catan.model.game;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class CurrentMove {

    private Color color;
    private int roll;
    private DevCard devCard;
    private boolean thiefMove;
    // TODO: trades
}
