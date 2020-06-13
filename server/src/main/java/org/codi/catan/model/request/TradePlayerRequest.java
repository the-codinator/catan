/*
 * @author the-codinator
 * created on 2020/6/13
 */

package org.codi.catan.model.request;

import lombok.Getter;
import lombok.Setter;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.Resource;

@Getter
@Setter
public class TradePlayerRequest {

    private Color partner;
    private Resource[] offer;
    private Resource[] ask;
}
