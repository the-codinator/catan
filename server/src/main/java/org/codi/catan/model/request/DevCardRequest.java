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
    private Resource resource1; // for monopoly & year of plenty
    private Resource resource2; // for year of plenty
    private RoadRequest road1; // for road building
    private RoadRequest road2; // for road building
    private int thief; // for knight
    private Color color; // for knight
}
