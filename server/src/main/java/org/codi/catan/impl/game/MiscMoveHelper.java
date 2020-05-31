/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.impl.game;

import javax.inject.Singleton;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.CurrentMove;
import org.codi.catan.model.game.Phase;
import org.codi.catan.model.game.State;
import org.codi.catan.util.Util;

@Singleton
public class MiscMoveHelper { // TODO:

    /**
     * Complete a turn
     */
    public void endTurn(Board board, State state) {
        Color color = state.getCurrentMove().getColor();
        int index = Util.find(board.getPlayers(), p -> p.getColor() == color);
        int minIndex = 0;
        int maxIndex = board.getPlayers().length - 1;
        switch (state.getPhase()) {
            case setup1:
                if (index < maxIndex) {
                    index++;
                } else {
                    state.setPhase(Phase.setup2);
                }
                break;
            case setup2:
                if (index > minIndex) {
                    index--;
                } else {
                    state.setPhase(Phase.gameplay);
                }
                break;
            default:
                index++;
                if (index > maxIndex) {
                    index = minIndex;
                }
        }
        state.setCurrentMove(new CurrentMove(board.getPlayers()[index].getColor()));
    }
}
