/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.impl.game;

import javax.inject.Singleton;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.Tile;

@Singleton
public class FindUtility {

    /**
     * Find the file position of the thief on the {@param board}
     */
    public int findThief(Board board) throws CatanException {
        Tile[] tiles = board.getTiles();
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i].getResource() == null) {
                return i;
            }
        }
        throw new CatanException("Invalid Board");
    }
}
