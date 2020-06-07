/*
 * @author the-codinator
 * created on 2020/6/7
 */

package org.codi.catan.core;

import javax.ws.rs.core.Response.Status;

public class BadRequestException extends CatanException {

    public BadRequestException(String badMoveMessage) {
        super(badMoveMessage, Status.BAD_REQUEST);
    }
}
