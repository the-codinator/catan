/*
 * @author the-codinator
 * created on 2020/5/31
 */

package org.codi.catan.impl.game;

import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.State;

@FunctionalInterface
public interface MoveRequestLessHandler extends MoveRequestHandler<Object> {

    @Override
    default boolean shouldValidateInput() {
        return false;
    }

    @Override
    default void play(Board board, State state, Object request) throws CatanException {
        play(board, state);
    }

    void play(Board board, State state) throws CatanException;
}
