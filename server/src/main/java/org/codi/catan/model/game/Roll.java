/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Roll {

    private int die1;
    private int die2;

    public int getRoll() {
        return die1 + die2;
    }
}
