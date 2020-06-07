/*
 * @author the-codinator
 * created on 2020/5/28
 */

package org.codi.catan.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Phase {
    setup1(true),
    setup2(true),
    gameplay,
    thief,
    end;

    Phase() {
        this(false);
    }

    private final boolean setupPhase;
}
