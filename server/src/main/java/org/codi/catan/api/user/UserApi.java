/*
 * @author the-codinator
 * created on 2020/5/23
 */

package org.codi.catan.api.user;

import static org.codi.catan.util.Constants.API_USER;
import static org.codi.catan.util.Constants.BASE_PATH_USER;
import static org.codi.catan.util.Constants.BEARER_AUTHORIZATION_KEY;
import static org.codi.catan.util.Constants.PARAM_ONGOING;
import static org.codi.catan.util.Constants.PARAM_REMEMBER_ME;
import static org.codi.catan.util.Constants.PARAM_USER_ID;
import static org.codi.catan.util.Constants.PATH_FIND;
import static org.codi.catan.util.Constants.PATH_GAMES;
import static org.codi.catan.util.Constants.PATH_LOGIN;
import static org.codi.catan.util.Constants.PATH_LOGOUT;
import static org.codi.catan.util.Constants.PATH_REFRESH;
import static org.codi.catan.util.Constants.PATH_SIGNUP;
import static org.codi.catan.util.Constants.TOKEN;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import java.util.Collection;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.impl.user.UserApiHelper;
import org.codi.catan.impl.user.UserGamesHelper;
import org.codi.catan.model.request.LoginRequest;
import org.codi.catan.model.request.RefreshTokenRequest;
import org.codi.catan.model.request.SignUpRequest;
import org.codi.catan.model.response.FindUserResponse;
import org.codi.catan.model.response.MessageResponse;
import org.codi.catan.model.response.SessionResponse;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.User;

@Api(API_USER)
@Path(BASE_PATH_USER)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserApi {

    private final UserApiHelper userApiHelper;
    private final UserGamesHelper userGamesHelper;

    @Inject
    public UserApi(UserApiHelper userApiHelper, UserGamesHelper userGamesHelper) {
        this.userApiHelper = userApiHelper;
        this.userGamesHelper = userGamesHelper;
    }

    @POST
    @Path(PATH_SIGNUP)
    public MessageResponse signup(SignUpRequest request) throws CatanException {
        userApiHelper.signup(request);
        return new MessageResponse(Status.OK, "User Created Successfully!");
    }

    @POST
    @Path(PATH_LOGIN)
    public SessionResponse login(LoginRequest request, @QueryParam(PARAM_REMEMBER_ME) Boolean rememberMe)
        throws CatanException {
        return userApiHelper.login(request, rememberMe == null ? false : rememberMe);
    }

    @POST
    @Path(PATH_REFRESH)
    public SessionResponse refresh(RefreshTokenRequest request) throws CatanException {
        return userApiHelper.refresh(request);
    }

    @POST
    @Path(PATH_LOGOUT)
    @ApiOperation(value = "", authorizations = @Authorization(BEARER_AUTHORIZATION_KEY))
    @PermitAll
    public MessageResponse logout(@Context ContainerRequestContext request) throws CatanException {
        Token token = (Token) request.getProperty(TOKEN);
        userApiHelper.logout(token);
        return new MessageResponse(Status.OK, "User Logged Out Successfully!");
    }

    @GET
    @Path(PATH_FIND)
    @ApiOperation(value = "", authorizations = @Authorization(BEARER_AUTHORIZATION_KEY))
    @PermitAll
    public Collection<FindUserResponse> find(@QueryParam(PARAM_USER_ID) String userId) throws CatanException {
        return userApiHelper.find(userId);
    }

    @GET
    @Path(PATH_GAMES)
    @ApiOperation(value = "", authorizations = @Authorization(BEARER_AUTHORIZATION_KEY))
    @PermitAll
    public List<String> games(@ApiParam(hidden = true) @Auth User user, @QueryParam(PARAM_ONGOING) Boolean ongoing)
        throws CatanException {
        return userGamesHelper.games(user.getId(), ongoing);
    }
}
