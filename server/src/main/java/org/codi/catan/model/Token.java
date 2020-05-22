package org.codi.catan.model;

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
    private long created;
    private long expiry;

    public Token(String id) {
        this();
        this.id = id;
    }

    public enum TokenType {
        access,
        refresh
    }
}
