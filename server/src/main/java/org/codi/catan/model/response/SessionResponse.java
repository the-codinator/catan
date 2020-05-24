/*
 * @author the-codinator
 * created on 2020/5/23
 */

package org.codi.catan.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.codi.catan.model.user.Role;

@AllArgsConstructor
@Getter
@JsonInclude(Include.NON_NULL)
public class SessionResponse {

    private final String id;
    private final String name;
    private final Set<Role> roles;
    private final long created;
    @JsonProperty("access_token")
    private final String accessToken;
    @JsonProperty("refresh_token")
    private final String refreshToken;
}
