/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.core;

import javax.ws.rs.core.Response.Status;
import org.codi.catan.model.response.MessageResponse;

public class CatanException extends Exception {

    public static final Status DEFAULT_ERROR_STATUS = Status.INTERNAL_SERVER_ERROR;
    private final Status errorStatus;

    public CatanException(String message) {
        this(message, (Status) null);
    }

    public CatanException(String message, Exception e) {
        super(message, e);
        this.errorStatus = e instanceof CatanException ? ((CatanException) e).errorStatus : null;
    }

    public CatanException(String message, Status errorStatus) {
        super(message);
        this.errorStatus = errorStatus;
    }

    public CatanException(String message, Status errorStatus, Exception e) {
        super(message, e);
        this.errorStatus = errorStatus;
    }

    public Status getErrorStatus() {
        return errorStatus == null ? DEFAULT_ERROR_STATUS : errorStatus;
    }

    public static Status getErrorStatus(Exception e) {
        return e instanceof CatanException ? ((CatanException) e).getErrorStatus() : DEFAULT_ERROR_STATUS;
    }

    @Override
    public String getMessage() {
        if (errorStatus != null) {
            return String.format("[ code=%d ] %s", errorStatus.getStatusCode(), super.getMessage());
        } else {
            return super.getMessage();
        }
    }

    public MessageResponse asMessageResponse() {
        return new MessageResponse(getErrorStatus().getStatusCode(), super.getMessage());
    }

    public static MessageResponse asMessageResponse(Exception e) {
        return e instanceof CatanException ? ((CatanException) e).asMessageResponse()
            : new MessageResponse(DEFAULT_ERROR_STATUS, "Unhandled Error: " + e.getMessage());
    }
}
