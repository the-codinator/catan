/*
 * @author the-codinator
 * created on 2020/5/24
 */

package org.codi.catan.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {

    @JsonProperty("refresh_token")
    private String refreshToken;
}
