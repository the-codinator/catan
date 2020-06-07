/*
 * @author the-codinator
 * created on 2020/6/7
 */

package org.codi.catan.impl.handler;

import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.State;

@FunctionalInterface
public interface BoardStateRequestMoveHandler<T> {

    default boolean shouldValidateInput() {
        return true;
    }

    default BoardStateRequestMoveHandler<T> asBaseType() {
        return this;
    }

    void play(Board board, State state, T request) throws CatanException;
}
