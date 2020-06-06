/*
 * @author the-codinator
 * created on 2020/5/26
 */

package org.codi.catan.model.game;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codi.catan.model.core.IdentifiableEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Board implements IdentifiableEntity {

    public Board(String id) {
        this.id = id;
    }

    private String id;
    private Tile[] tiles;
    private Ports ports;
    private Player[] players;
}
