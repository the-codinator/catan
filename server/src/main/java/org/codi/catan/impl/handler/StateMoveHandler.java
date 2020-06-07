/*
 * @author the-codinator
 * created on 2020/6/7
 */

package org.codi.catan.impl.handler;

import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.State;

@FunctionalInterface
public interface StateMoveHandler extends BoardStateMoveHandler {

    @Override
    default void play(Board board, State state) throws CatanException {
        play(state);
    }

    void play(State state) throws CatanException;
}
