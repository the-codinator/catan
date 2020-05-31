/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.model.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AchievementValue {

    public AchievementValue(int count) {
        this.color = null;
        this.count = count;
    }

    private Color color;
    private int count;
}
