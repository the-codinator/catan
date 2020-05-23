/*
 * @author the-codinator
 * created on 2020/5/23
 */

package org.codi.catan.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SessionResponse {

    private final String id;
    private final String name;
    @JsonInclude(Include.NON_DEFAULT)
    private final boolean admin;
    private final String accessToken;
    @JsonInclude(Include.NON_NULL)
    private final String refreshToken;
}
