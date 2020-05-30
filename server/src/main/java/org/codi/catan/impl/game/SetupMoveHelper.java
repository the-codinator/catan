/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.impl.game;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.State;
import org.codi.catan.model.request.SetupMoveRequest;

@Singleton
public class SetupMoveHelper {

    private final GameUtility gameUtility;

    @Inject
    public SetupMoveHelper(GameUtility gameUtility) {
        this.gameUtility = gameUtility;
    }

    public void play(Board board, State state, SetupMoveRequest request) {
        // TODO:
    }
}
