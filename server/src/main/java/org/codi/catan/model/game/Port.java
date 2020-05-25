/*
 * @author the-codinator
 * created on 2020/5/26
 */

package org.codi.catan.model.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Port {

    private int vertex;
    private Resource resource; // null => 3:1 Port
}
