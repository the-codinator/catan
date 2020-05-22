/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.core;

import javax.ws.rs.core.Response.Status;
import org.codi.catan.model.ErrorResponse;

public class CatanException extends Exception {

    public static final Status DEFAULT_ERROR_STATUS = Status.INTERNAL_SERVER_ERROR;
    private final Status errorStatus;

    public CatanException(String message) {
        this(message, (Status) null);
    }

    public CatanException(String message, Exception e) {
        super(message, e);
        if (e instanceof CatanException) {
            this.errorStatus = ((CatanException) e).errorStatus;
        } else {
            this.errorStatus = null;
        }
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

    public ErrorResponse asErrorResponse() {
        return new ErrorResponse(getErrorStatus().getStatusCode(), super.getMessage());
    }

    public static ErrorResponse asErrorResponse(Exception e) {
        return e instanceof CatanException ? ((CatanException) e).asErrorResponse()
            : new ErrorResponse(DEFAULT_ERROR_STATUS.getStatusCode(), "Unhandled Error: " + e.getMessage());
    }
}
