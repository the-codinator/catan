/*
 * @author the-codinator
 * created on 2020/5/23
 */

package org.codi.catan.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest extends LoginRequest {

    private String name;
}
