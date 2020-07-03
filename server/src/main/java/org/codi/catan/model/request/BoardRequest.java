/*
 * @author the-codinator
 * created on 2020/7/3
 */

package org.codi.catan.model.request;

import lombok.Getter;
import lombok.Setter;
import org.codi.catan.model.game.Player;
import org.codi.catan.model.game.Ports;
import org.codi.catan.model.game.Tile;

@Getter
@Setter
public class BoardRequest {
    private Tile[] tiles;
    private Ports ports;
    private Player[] players;
}
