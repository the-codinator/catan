/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorResponse {

    private final int code;
    private final String message;
}
