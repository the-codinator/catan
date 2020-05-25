/*
 * @author the-codinator
 * created on 2020/5/26
 */

package org.codi.catan.model.response;

import lombok.Getter;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.State;

@Getter
public class GameResponse {

    public GameResponse(Board board, State state) {
        this.id = board.getId();
        this.board = board;
        this.state = state;
    }

    private final String id;
    private final Board board;
    private final State state;
}
