/*
 * @author the-codinator
 * created on 2020/5/28
 */

package org.codi.catan.model.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class House {

    private HouseType type;
    private Color color;
    private int vertex;
}
