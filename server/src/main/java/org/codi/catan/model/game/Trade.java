/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.model.game;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.EnumMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Trade {

    private Color partner;
    private boolean offeredByPartner;
    @JsonInclude(Include.NON_NULL)
    private EnumMap<Resource, Integer> partnerResources;
    @JsonInclude(Include.NON_NULL)
    private EnumMap<Resource, Integer> turnResources;
}
