/*
 * @author the-codinator
 * created on 2020/5/23
 */

package org.codi.catan.impl.user;

import static org.codi.catan.util.Constants.NAME_REGEX;
import static org.codi.catan.util.Constants.USER_ID_REGEX;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.impl.data.CatanDataConnector;
import org.codi.catan.model.request.LoginRequest;
import org.codi.catan.model.request.RefreshTokenRequest;
import org.codi.catan.model.request.SignUpRequest;
import org.codi.catan.model.response.SessionResponse;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.Token.TokenType;
import org.codi.catan.model.user.User;
import org.codi.catan.util.Util;

@Singleton
public class UserApiHelper {

    private final SessionHelper sessionHelper;
    private final CatanDataConnector dataConnector;

    @Inject
    public UserApiHelper(SessionHelper sessionHelper, CatanDataConnector dataConnector) {
        this.sessionHelper = sessionHelper;
        this.dataConnector = dataConnector;
    }

    @SuppressWarnings({"checkstyle:MissingJavadocMethod", "checkstyle:LineLength"})
    public void validateUserId(String id) throws CatanException {
        if (id == null || !id.matches(USER_ID_REGEX)) {
            throw new CatanException(
                "User Id must only contain (english) alphabets, (arabic) numerals, hyphen (-), underscore(_), and must be between 3 and 12 characters",
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

    /**
     * Validate that user id and password match
     */
    public void validateCredentials(User actual, User expected) throws CatanException {
        if (actual.getId() == null || !actual.getId().equals(expected.getId())) {
            // Ideally shouldn't happen
            throw new CatanException("Expected user is different");
        }
        if (actual.getPwd() == null || !actual.getPwd().equals(expected.getPwd())) {
            throw new CatanException("Username/Password Mismatch", Status.UNAUTHORIZED);
        }
    }

    /**
     * User Sign Up (user creation) flow
     */
    public void signup(SignUpRequest request) throws CatanException {
        Util.validateInput(request);
        validateUserId(request.getId());
        validateName(request.getName());
        validatePwd(request.getPwd());
        User user = new User(request.getId(), request.getName(), request.getPwd(), null);
        if (!dataConnector.createUser(user)) {
            throw new CatanException("User id is already taken!", Status.CONFLICT);
        }
    }

    /**
     * User Login flow
     */
    public SessionResponse login(LoginRequest request, boolean rememberMe) throws CatanException {
        Util.validateInput(request);
        validateUserId(request.getId());
        validatePwd(request.getPwd());
        User user = new User(request.getId(), null, request.getPwd(), null);
        return loginInternal(user, rememberMe);
    }

    /**
     * Session Refresh flow (Remember me scenario)
     */
    public SessionResponse refresh(RefreshTokenRequest request) throws CatanException {
        Util.validateInput(request);
        Token token;
        try {
            token = sessionHelper.parseToken(request.getRefreshToken());
        } catch (CatanException e) {
            throw new CatanException("Incorrect Token Type", Status.BAD_REQUEST, e);
        }
        if (token == null) {
            throw new CatanException("Missing Refresh Token", Status.BAD_REQUEST);
        }
        if (token.getType() != TokenType.refresh) {
            throw new CatanException("Bad Refresh Token", Status.BAD_REQUEST);
        }
        sessionHelper.validateRequestTokenOffline(token);
        Token dbToken = dataConnector.getToken(token.getId());
        if (!token.equals(dbToken)) {
            throw new CatanException("Invalid Token", Status.BAD_REQUEST);
        }
        logout(token);
        User user = new User(token.getUser());
        user.setRoles(token.getRoles());
        return createSessionInternal(user, true);
    }

    private SessionResponse loginInternal(User requestUser, boolean rememberMe) throws CatanException {
        User dbUser = dataConnector.getUser(requestUser.getId());
        if (dbUser == null) {
            throw new CatanException("Username/Password Mismatch", Status.UNAUTHORIZED);
        }
        validateCredentials(requestUser, dbUser);
        return createSessionInternal(dbUser, rememberMe);
    }

    private SessionResponse createSessionInternal(User dbUser, boolean rememberMe) throws CatanException {
        Token accessToken = sessionHelper.createSession(dbUser, TokenType.access);
        Token refreshToken = null;
        if (rememberMe) {
            refreshToken = sessionHelper.createSession(dbUser, TokenType.refresh);
            accessToken.setLinkedId(refreshToken.getId());
            refreshToken.setLinkedId(accessToken.getId());
        }
        if (!(dataConnector.createToken(accessToken) && (refreshToken == null || dataConnector.createToken(
            refreshToken)))) {
            throw new CatanException("Conflicting Token Id");
        }
        return new SessionResponse(dbUser.getId(), dbUser.getName(), dbUser.getRoles(), accessToken.getCreated(),
            sessionHelper.serializeToken(accessToken), sessionHelper.serializeToken(refreshToken));
    }

    public void logout(Token token) throws CatanException {
        dataConnector.deleteToken(token.getId());
        if (token.getLinkedId() != null) {
            dataConnector.deleteToken(token.getLinkedId());
        }
    }
}
