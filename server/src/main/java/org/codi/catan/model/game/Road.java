/*
 * @author the-codinator
 * created on 2020/5/28
 */

package org.codi.catan.model.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Road {

    private Color color;
    private int vertex1;
    private int vertex2;
}
