/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.model;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    private String id;
    private String name;
    private String pwd;
    private Set<UserRole> roles;
    private boolean isAdmin; // For simplicity we us a single var since we don't have multiple roles

    public enum UserRole {
        ADMIN
    }
}



