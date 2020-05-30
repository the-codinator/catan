/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.impl.game;

import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.State;

@FunctionalInterface
public interface MoveRequestHandler<T> {

    void play(Board board, State state, T request) throws CatanException;
}
