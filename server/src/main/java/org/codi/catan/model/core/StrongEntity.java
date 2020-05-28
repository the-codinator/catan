/*
 * @author the-codinator
 * created on 2020/5/28
 */

package org.codi.catan.model.core;

/**
 * The ETag value is defined by one of the following:
 * - ETag value of the newly created {@link StrongEntity} derivative object, used for next operation
 * - ETag value of the old {@link StrongEntity} derivative object, used for current operation
 */
public interface StrongEntity {

    String getETag();

    void setETag(String etag);
}
