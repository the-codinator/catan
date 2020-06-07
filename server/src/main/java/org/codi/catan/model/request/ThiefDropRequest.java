/*
 * @author the-codinator
 * created on 2020/6/7
 */

package org.codi.catan.model.request;

import lombok.Getter;
import lombok.Setter;
import org.codi.catan.model.game.Resource;

@Getter
@Setter
public class ThiefDropRequest {

    private Resource[] resources;
}
