/*
 * @author the-codinator
 * created on 2020/5/23
 */

package org.codi.catan.impl.user;

import static org.codi.catan.util.Constants.NAME_REGEX;
import static org.codi.catan.util.Constants.USER_ID_REGEX;

import com.google.inject.Inject;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.impl.data.CatanDataConnector;
import org.codi.catan.model.request.LoginRequest;
import org.codi.catan.model.request.SignUpRequest;
import org.codi.catan.model.response.SessionResponse;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.Token.TokenType;
import org.codi.catan.model.user.User;
import org.codi.catan.util.Util;

public class UserApiHelper {

    private final SessionHelper sessionHelper;
    private final CatanDataConnector dataConnector;

    @Inject
    public UserApiHelper(SessionHelper sessionHelper, CatanDataConnector dataConnector) {
        this.sessionHelper = sessionHelper;
        this.dataConnector = dataConnector;
    }

    public void validateUserId(String id) throws CatanException {
        if (id == null || !id.matches(USER_ID_REGEX)) {
            throw new CatanException(
                "User Id must contain only alphabets, numbers, hyphen, or underscore, and must be between 3 and 12 characters",
                Status.BAD_REQUEST);
        }
    }

    public void validatePwd(String pwd) throws CatanException {
        // Note: `pwd` here is the password hash
        if (pwd == null || pwd.length() < 3 || pwd.length() > 100) {
            throw new CatanException("Please use a reasonable passwords", Status.BAD_REQUEST);
        }
    }

    public void validateName(String name) throws CatanException {
        if (name == null || !name.matches(NAME_REGEX)) {
            throw new CatanException("Name does not belong to a human", Status.BAD_REQUEST);
        }
    }

    public void validateCredentials(User actual, User expected) throws CatanException {
        if (actual.getId() == null || !actual.getId().equals(expected.getId())) {
            throw new CatanException("Expected user is different");
        }
        if (actual.getPwd() == null || !actual.getPwd().equals(expected.getPwd())) {
            throw new CatanException("Username/Password Mismatch", Status.UNAUTHORIZED);
        }
    }

    public void signup(SignUpRequest request) throws CatanException {
        Util.validateInput(request);
        validateUserId(request.getId());
        validateName(request.getName());
        validatePwd(request.getPwd());
        User user = new User(request.getId(), request.getName(), request.getPwd(), false);
        dataConnector.createUser(user);
    }

    public SessionResponse login(LoginRequest request, boolean rememberMe) throws CatanException {
        Util.validateInput(request);
        validateUserId(request.getId());
        validatePwd(request.getPwd());
        User user = new User(request.getId(), null, request.getPwd(), false);
        return loginInternal(user, rememberMe);
    }

    private SessionResponse loginInternal(User requestUser, boolean rememberMe) throws CatanException {
        User dbUser;
        try {
            dbUser = dataConnector.getUser(requestUser);
        } catch (CatanException e) {
            throw new CatanException("Username/Password Mismatch", Status.UNAUTHORIZED);
        }
        validateCredentials(requestUser, dbUser);
        Token accessToken = sessionHelper.createSession(dbUser, TokenType.access);
        dataConnector.createToken(accessToken);
        Token refreshToken = null;
        if (rememberMe) {
            refreshToken = sessionHelper.createSession(dbUser, TokenType.refresh);
            dataConnector.createToken(refreshToken);
        }
        return new SessionResponse(dbUser.getId(), dbUser.getName(), dbUser.isAdmin(),
            sessionHelper.serializeToken(accessToken), sessionHelper.serializeToken(refreshToken));
    }
}
