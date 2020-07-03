/*
 * @author the-codinator
 * created on 2020/5/26
 */

package org.codi.catan.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codi.catan.model.core.IdentifiableEntity;
import org.codi.catan.model.request.BoardRequest;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Board implements IdentifiableEntity {

    public Board(String id, BoardRequest request) {
        this(id, System.currentTimeMillis(), 0, request.getTiles(), request.getPorts(), request.getPlayers());
    }

    private String id;
    private long created;
    private long completed;
    private Tile[] tiles;
    private Ports ports;
    private Player[] players;
}
