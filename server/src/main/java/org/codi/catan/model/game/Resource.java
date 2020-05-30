/*
 * @author the-codinator
 * created on 2020/5/24
 */

package org.codi.catan.model.game;

import java.util.EnumMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Standard game resources
 * A {@code null} value can be used to indicate desert, 3:1 ports, etc.
 */
@Getter
@AllArgsConstructor
public enum Resource {
    brick(3),
    hay(4),
    sheep(4),
    rock(3),
    wood(4);

    private static final int RESOURCE_COUNT = 19;
    private final int tileCount;

    public static EnumMap<Resource, Integer> createNewBank() {
        EnumMap<Resource, Integer> map = new EnumMap<>(Resource.class);
        for (Resource r : values()) {
            map.put(r, RESOURCE_COUNT);
        }
        return map;
    }
}
