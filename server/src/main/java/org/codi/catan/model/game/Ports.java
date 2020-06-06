/*
 * @author the-codinator
 * created on 2020/5/26
 */

package org.codi.catan.model.game;

import java.util.EnumMap;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Ports {

    private EnumMap<Resource, Integer> ports21;
    private Set<Integer> ports31;
}
