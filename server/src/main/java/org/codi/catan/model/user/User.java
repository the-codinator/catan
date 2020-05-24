/*
 * @author the-codinator
 * created on 2020/5/23
 */

package org.codi.catan.model.user;

import java.security.Principal;
import java.util.Set;
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
public class User implements Principal {

    private String id;
    private String name;
    private String pwd;
    /*
     * Ideally ACLs should be stored in a separate DB but since it is different from authentication, and we don't want
     * password info to be available when checking permissions
     */
    private Set<Role> roles;

    public User(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getId();
    }
}
