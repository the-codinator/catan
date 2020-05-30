/*
 * @author the-codinator
 * created on 2020/5/28
 */

package org.codi.catan.model.game;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import lombok.Setter;

@Setter
public class Hand {

    @JsonInclude(Include.NON_NULL)
    private EnumMap<Resource, Integer> resources;
    private List<DevCard> devCards;

    public EnumMap<Resource, Integer> getResources() {
        if (resources == null) {
            resources = new EnumMap<>(Resource.class);
        }
        return resources;
    }

    public List<DevCard> getDevCards() {
        if (devCards == null) {
            devCards = new ArrayList<>(1);
        }
        return devCards;
    }
}
