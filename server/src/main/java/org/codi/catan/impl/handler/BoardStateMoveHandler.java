/*
 * @author the-codinator
 * created on 2020/6/7
 */

package org.codi.catan.impl.handler;

import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.State;

@FunctionalInterface
public interface BoardStateMoveHandler extends BoardStateRequestMoveHandler<Void> {

    @Override
    default boolean shouldValidateInput() {
        return false;
    }

    @Override
    default void play(Board board, State state, Void request) throws CatanException {
        play(board, state);
    }

    void play(Board board, State state) throws CatanException;
}
