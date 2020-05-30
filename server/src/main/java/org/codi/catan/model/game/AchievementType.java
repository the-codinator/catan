/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AchievementType {
    longest_road(2),
    largest_army(2);

    private final int victoryPoints;
}
