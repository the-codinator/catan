/*
 * @author the-codinator
 * created on 2020/5/26
 */

package org.codi.catan.model.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tile {

    private Resource resource; // null => Desert
    private int roll;
}
