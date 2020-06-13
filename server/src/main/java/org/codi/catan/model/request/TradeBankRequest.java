/*
 * @author the-codinator
 * created on 2020/6/13
 */

package org.codi.catan.model.request;

import lombok.Getter;
import lombok.Setter;
import org.codi.catan.model.game.Resource;

@Getter
@Setter
public class TradeBankRequest {

    private Resource offer;
    private int count;
    private Resource ask;
}
