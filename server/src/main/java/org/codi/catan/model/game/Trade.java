/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.model.game;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.EnumMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trade { // TODO:

    // Trade ID is defined by its index in State.currentMove.trades
    private String partner;
    private boolean offeredByPartner;
    @JsonInclude(Include.NON_NULL)
    private EnumMap<Resource, Integer> turnResources;
    @JsonInclude(Include.NON_NULL)
    private EnumMap<Resource, Integer> partnerResources;
}
