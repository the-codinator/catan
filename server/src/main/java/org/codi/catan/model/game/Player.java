/*
 * @author the-codinator
 * created on 2020/5/27
 */

package org.codi.catan.model.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {

    private String id; // TODO: Store display names as well / cache somehow ?
    private Color color;
}
