/*
 * @author the-codinator
 * created on 2020/6/7
 */

package org.codi.catan.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codi.catan.model.game.Color;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ThiefPlayRequest {

    private int hex;
    private Color victim;
}
