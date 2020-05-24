/*
 * @author the-codinator
 * created on 2020/5/24
 */

package org.codi.catan.impl.auth;

import io.dropwizard.auth.Authorizer;
import org.codi.catan.model.user.Role;
import org.codi.catan.model.user.User;

public class CatanAuthorizer implements Authorizer<User> {

    @Override
    @SuppressWarnings("deprecation")
    public boolean authorize(User user, String role) {
        return user != null && user.getRoles() != null && user.getRoles().contains(Role.valueOf(role));
    }
}
