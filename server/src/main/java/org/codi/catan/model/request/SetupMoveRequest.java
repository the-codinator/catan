/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetupMoveRequest {

    private int houseVertex;
    private int roadVertex;
}
