/*
 * @author the-codinator
 * created on 2020/5/23
 */

package org.codi.catan.model.response;

import javax.ws.rs.core.Response.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MessageResponse {

    public MessageResponse(Status status, String message) {
        this(status.getStatusCode(), message);
    }

    private final int code;
    private final String message;
}
