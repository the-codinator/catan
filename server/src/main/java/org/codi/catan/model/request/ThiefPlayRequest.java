/*
 * @author the-codinator
 * created on 2020/6/7
 */

package org.codi.catan.model.request;

import lombok.Getter;
import lombok.Setter;
import org.codi.catan.model.game.Color;

@Getter
@Setter
public class ThiefPlayRequest {

    private int hex;
    private Color color; // Who we are stealing a card from
}
