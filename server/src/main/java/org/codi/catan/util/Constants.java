/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.util;

public class Constants {

    // Headers, Params, etc
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_ETAG = "ETag";
    public static final String HEADER_IF_MATCH = "If-Match";
    public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    public static final String PARAM_GAME_ID = "game";
    public static final String PARAM_REMEMBER_ME = "rememberMe";
    public static final String PARAM_TRADE_ID = "trade";
    public static final String PARAM_USER_ID = "user";

    // API
    public static final String API_BOARD = "Game State APIs";
    public static final String API_MOVE = "Game Move APIs";
    public static final String API_PING = "Ping Check";
    public static final String API_USER = "User APIs";

    // Paths - Core
    public static final String PATH_FAVICON = "/favicon.ico";
    public static final String PATH_HEALTH = "/healthcheck";
    public static final String PATH_PING = "/ping";

    // Paths - User
    public static final String BASE_PATH_USER = "/user";
    public static final String PATH_FIND = "/find";
    public static final String PATH_GAMES = "/games";
    public static final String PATH_LOGIN = "/login";
    public static final String PATH_LOGOUT = "/logout";
    public static final String PATH_REFRESH = "/refresh";
    public static final String PATH_SIGNUP = "/signup";

    // Paths - Game
    public static final String BASE_PATH_GAME = "/game";
    public static final String PATH_GAME_ID = "/{" + PARAM_GAME_ID + "}";
    public static final String PATH_BOARD = PATH_GAME_ID + "/board";
    public static final String PATH_STATE = PATH_GAME_ID + "/state";

    // Paths - Move
    public static final String BASE_PATH_MOVE = BASE_PATH_GAME + PATH_GAME_ID + "/move";
    public static final String PATH_BUILD = "/build";
    public static final String PATH_BUILD_HOUSE = PATH_BUILD + "/house";
    public static final String PATH_BUILD_ROAD = PATH_BUILD + "/road";
    public static final String BASE_PATH_DEV = BASE_PATH_MOVE + "/dev";
    public static final String PATH_DEV_BUY = "/buy";
    public static final String PATH_DEV_PLAY = "/play";
    public static final String PATH_END = "/end";
    public static final String PATH_ROLL = "/roll";
    public static final String PATH_SETUP = "/setup";
    public static final String PATH_THIEF = "/thief";
    public static final String PATH_THIEF_DROP = PATH_THIEF + "/drop";
    public static final String PATH_THIEF_PLAY = PATH_THIEF + "/play";

    // Paths - Trade
    public static final String BASE_PATH_TRADE = BASE_PATH_MOVE + "/trade";
    public static final String PATH_TRADE_ID = "/{" + PARAM_TRADE_ID + "}";
    public static final String PATH_TRADE_ACCEPT = PATH_TRADE_ID + "/accept";
    public static final String PATH_TRADE_GAME = "/game";
    public static final String PATH_TRADE_PLAYER = "/player";
    public static final String PATH_TRADE_REJECT = PATH_TRADE_ID + "/reject";

    // Validators
    public static final String NAME_REGEX = "[\\w-'\\. ]{2,50}";
    public static final String USER_ID_REGEX = "[\\w-]{3,12}";

    // Game
    public static final int DICE_COUNT = 2;
    public static final int DROP_CARDS_FOR_THIEF_THRESHOLD = 7;
    public static final int MAX_HOUSES_PER_PLAYER = 5;
    public static final int MAX_ROADS_PER_PLAYER = 14;
    public static final int MAX_ROLL_PER_DIE = 6;
    public static final int MIN_ROLL_PER_DIE = 1;
    public static final int THIEF_ROLL = 7;
    public static final int VICTORY_POINTS_FOR_WIN = 10;

    // Error Messages
    public static final String ENTITY_CONFLICT = "Entity [%s] with id [%s] already exists";
    public static final String ENTITY_NOT_FOUND = "Entity [%s] with id [%s] not found";
    public static final String ENTITY_PRECONDITION_FAILED = "Entity [%s] with id [%s] does not exist";

    // Misc
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String BEARER_AUTHORIZATION_KEY = "Bearer [access_token]";
    public static final long DAY_MILLIS = 24 * 60 * 60 * 1000L;
    public static final String DELEGATE = "delegate";
    public static final int[] EMPTY_INT_ARRAY = new int[0];
    public static final int MAX_ONGOING_GAMES_PER_USER = 4;
    public static final int MAX_RECENTLY_COMPLETED_GAMES_PER_USER = 10;
    public static final String TOKEN = "token";
}
