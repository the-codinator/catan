/*
 * @author the-codinator
 * created on 2020/6/13
 */

package org.codi.catan.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeResponseRequest {

    private String id;
    private boolean accepted;
}
