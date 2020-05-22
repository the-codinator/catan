/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.user;

import org.codi.catan.model.User;
import org.codi.catan.model.User.UserRole;

public class UserHelper {

    public boolean isAdmin(User user) {
        return user != null && user.getRoles().contains(UserRole.ADMIN);
    }
}
