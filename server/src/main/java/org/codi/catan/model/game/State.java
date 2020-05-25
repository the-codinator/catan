/*
 * @author the-codinator
 * created on 2020/5/26
 */

package org.codi.catan.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codi.catan.model.IdentifiableEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class State implements IdentifiableEntity {

    public State(String id) {
        this.id = id;
    }

    private String id;
    private int thief;
    // TODO:
}
