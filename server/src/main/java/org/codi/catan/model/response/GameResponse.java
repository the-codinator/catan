/*
 * @author the-codinator
 * created on 2020/5/26
 */

package org.codi.catan.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.codi.catan.model.core.StrongEntity;
import org.codi.catan.model.game.Board;

@Getter
public class GameResponse implements StrongEntity {

    public GameResponse(Board board, StateResponse state) {
        this.id = board.getId();
        this.board = board;
        this.state = state;
        this.eTag = state.getETag();
    }

    private final String id;
    private final Board board;
    private final StateResponse state;
    @SuppressWarnings("checkstyle:MemberName")
    @Setter
    private String eTag;

    @Override
    @JsonIgnore
    public String getETag() {
        return this.eTag;
    }
}
