/*
 * @author the-codinator
 * created on 2020/5/23
 */

package org.codi.catan.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Token {

    private String id;
    private TokenType type;
    private String user;
    @JsonInclude(Include.NON_DEFAULT)
    private boolean admin;
    private Long created;
    private Long expires;

    public Token(String id) {
        this();
        this.id = id;
    }

    public enum TokenType {
        access,
        refresh
    }
}
