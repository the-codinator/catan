/*
 * @author the-codinator
 * created on 2020/6/12
 */

package org.codi.catan.model.admin;

import lombok.Getter;
import lombok.Setter;
import org.codi.catan.model.game.State;

@Getter
@Setter
public class AdminRequest {

    private AdminAction action;
    private String id;
    private State state;
}
