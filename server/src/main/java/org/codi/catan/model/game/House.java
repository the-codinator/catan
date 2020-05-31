/*
 * @author the-codinator
 * created on 2020/5/28
 */

package org.codi.catan.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class House {

    private Color color;
    private HouseType type;
    private int vertex;
}
