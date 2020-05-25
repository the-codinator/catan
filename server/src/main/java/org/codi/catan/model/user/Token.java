/*
 * @author the-codinator
 * created on 2020/5/23
 */

package org.codi.catan.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codi.catan.model.IdentifiableEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Token implements IdentifiableEntity {

    private String id;
    private TokenType type;
    private String user;
    @JsonInclude(Include.NON_DEFAULT)
    private Set<Role> roles;
    private Long created;
    private Long expires;
    private String linkedId;

    public Token(String id) {
        this();
        this.id = id;
    }

    public enum TokenType {
        access,
        refresh
    }
}
