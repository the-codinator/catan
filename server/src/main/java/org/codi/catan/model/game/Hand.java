/*
 * @author the-codinator
 * created on 2020/5/28
 */

package org.codi.catan.model.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.EnumMap;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Hand {

    @JsonInclude(Include.NON_NULL)
    private EnumMap<Resource, Integer> resources;
    private List<DevCard> devCards;

    @JsonIgnore
    public int getHandCount() {
        return resources.values().stream().mapToInt(x -> x).reduce(Integer::sum).orElse(0);
    }
}
