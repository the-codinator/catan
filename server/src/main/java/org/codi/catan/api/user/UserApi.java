/*
 * @author the-codinator
 * created on 2020/5/23
 */

package org.codi.catan.api.user;

import static org.codi.catan.util.Constants.API_USER;
import static org.codi.catan.util.Constants.PARAM_REMEMBER_ME;
import static org.codi.catan.util.Constants.PATH_LOGIN;
import static org.codi.catan.util.Constants.PATH_SIGNUP;
import static org.codi.catan.util.Constants.PATH_USER;

import com.google.inject.Inject;
import io.swagger.annotations.Api;
import java.util.Optional;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.impl.user.UserApiHelper;
import org.codi.catan.model.request.LoginRequest;
import org.codi.catan.model.request.SignUpRequest;
import org.codi.catan.model.response.MessageResponse;
import org.codi.catan.model.response.SessionResponse;

@Api(API_USER)
@Path(PATH_USER)
@Produces(MediaType.APPLICATION_JSON)
public class UserApi {

    private final UserApiHelper userApiHelper;

    @Inject
    public UserApi(UserApiHelper userApiHelper) {
        this.userApiHelper = userApiHelper;
    }

    @POST
    @Path(PATH_SIGNUP)
    public MessageResponse signup(SignUpRequest request) throws CatanException {
        userApiHelper.signup(request);
        return new MessageResponse(Status.OK, "User Created Successfully!");
    }

    @POST
    @Path(PATH_LOGIN)
    public SessionResponse login(LoginRequest request, @QueryParam(PARAM_REMEMBER_ME) Optional<Boolean> rememberMe)
        throws CatanException {
        return userApiHelper.login(request, rememberMe.orElse(false));
    }
}
