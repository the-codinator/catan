/*
 * @author the-codinator
 * created on 2020/5/28
 */

package org.codi.catan.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HouseType {
    settlement(1, 1),
    city(2, 2);

    private final int resourceMultiplier;
    private final int victoryPoints;
}
