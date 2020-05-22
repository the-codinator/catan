/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.model;

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
    /**
     * Ideally ACLs should be stored in a separate DB but since it is different from authentication, and we don't want
     * password info to be available when checking permissions
     *
     * Also, rather than independent ACL vars, we should have something like this:
     * <pre>{@code
     *   private Set<UserRole> roles;
     *
     *   public enum UserRole {
     *     ADMIN
     *   }
     * }</pre>
     *
     * Here since we have a very simple User definition and ACLs are only applicable for the admin APIs we can take a
     * shortcut with this and reduce some hassle
     */
    private Boolean admin;
}
