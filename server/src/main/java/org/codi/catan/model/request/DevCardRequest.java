/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.model.request;

import lombok.Getter;
import lombok.Setter;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.DevCard;
import org.codi.catan.model.game.Resource;

@Getter
@Setter
public class DevCardRequest {

    private DevCard type;

    // Monopoly
    private Resource resource;

    // Year of Plenty
    private Resource resource1;
    private Resource resource2;

    // Road Building
    private RoadRequest road1;
    private RoadRequest road2;

    // Knight
    private Integer hex;
    private Color victim;
}
