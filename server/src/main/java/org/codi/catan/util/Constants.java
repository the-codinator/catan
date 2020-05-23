/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.util;

public class Constants {

    // API
    public static final String API_PING = "Ping Check";
    public static final String API_USER = "User APIs";
    public static final String PARAM_REMEMBER_ME = "rememberMe";
    public static final String PATH_HEALTH = "/health";
    public static final String PATH_LOGIN = "/login";
    public static final String PATH_PING = "/ping";
    public static final String PATH_SIGNUP = "/signup";
    public static final String PATH_USER = "/user";

    // Headers, etc
    public static final String REQUEST_ID = "X-Request-Id";
    public static final String REQUEST_START_TIME = "requestStartTime";

    // Validators
    public static final String NAME_REGEX = "[\\w-'\\. ]{2,50}";
    public static final String USER_ID_REGEX = "[\\w-]{3,12}";

    // Misc
    public static final long DAY_MILLIS = 24 * 60 * 60 * 1000L;
    public static final String DELEGATE = "delegate";
}
