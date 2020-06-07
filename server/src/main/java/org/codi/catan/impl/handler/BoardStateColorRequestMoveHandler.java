/*
 * @author the-codinator
 * created on 2020/6/7
 */

package org.codi.catan.impl.handler;

import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.State;

@FunctionalInterface
public interface BoardStateColorRequestMoveHandler<T> {

    default boolean shouldValidateInput() {
        return true;
    }

    default BoardStateColorRequestMoveHandler<T> asBaseType() {
        return this;
    }

    void play(Board board, State state, Color color, T request) throws CatanException;
}
