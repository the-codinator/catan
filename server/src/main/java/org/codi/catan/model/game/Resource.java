/*
 * @author the-codinator
 * created on 2020/5/24
 */

package org.codi.catan.model.game;

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

    private final int tileCount;
}
