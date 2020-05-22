package org.codi.catan.impl.user;

import org.codi.catan.model.User;
import org.codi.catan.model.User.UserRole;

public class UserHelper {

    public boolean isAdmin(User user) {
        return user != null && user.getRoles().contains(UserRole.ADMIN);
    }
}
