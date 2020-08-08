/*
 * @author the-codinator
 * created on 2020/6/12
 */

package org.codi.catan.api.admin;

import static org.codi.catan.util.Constants.BEARER_AUTHORIZATION_KEY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.BadRequestException;
import org.codi.catan.core.CatanException;
import org.codi.catan.impl.data.CatanDataConnector;
import org.codi.catan.impl.game.MiscMoveHelper;
import org.codi.catan.impl.game.MoveApiHelper;
import org.codi.catan.impl.user.UserApiHelper;
import org.codi.catan.model.admin.AdminAction;
import org.codi.catan.model.admin.AdminRequest;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.OutOfTurnApi;
import org.codi.catan.model.game.Phase;
import org.codi.catan.model.game.Player;
import org.codi.catan.model.game.State;
import org.codi.catan.model.game.UserGame;
import org.codi.catan.model.user.Role;
import org.codi.catan.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Api(value = AdminApi.API_GROUP, authorizations = @Authorization(BEARER_AUTHORIZATION_KEY))
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(AdminApi.API_ENDPOINT)
@RolesAllowed(AdminApi.API_ROLE)
public class AdminApi {

    private static final Logger logger = LoggerFactory.getLogger(AdminApi.class);
    private static final String INITIAL_ADMIN_USER = "admin";
    static final String API_GROUP = "Admin API";
    static final String API_ENDPOINT = "/admin";
    /**
     * Maps to {@link org.codi.catan.model.user.Role#ADMIN}
     */
    static final String API_ROLE = "ADMIN";

    private final ObjectMapper objectMapper;
    private final CatanDataConnector dataConnector;
    private final MoveApiHelper moveApiHelper;
    private final MiscMoveHelper miscMoveHelper;

    @Inject
    public AdminApi(ObjectMapper objectMapper, CatanDataConnector dataConnector, MiscMoveHelper miscMoveHelper,
        UserApiHelper userApiHelper, MoveApiHelper moveApiHelper) {
        this.objectMapper = objectMapper;
        this.dataConnector = dataConnector;
        this.miscMoveHelper = miscMoveHelper;
        this.moveApiHelper = moveApiHelper;
        // Note: userApiHelper can never be a proxy object since no-once depends on AdminApi
        userApiHelper.setNewUserEventListener(this::handleNewUser);
    }

    @POST
    public Object admin(@ApiParam(hidden = true) @Auth User user, AdminRequest request) throws CatanException {
        try {
            logger.warn("[ ADMIN API ] [ {} ] request={}", user.getId(), objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new CatanException("Error logging request", e);
        }
        String id = request.getId();
        if (user.getId().equals(request.getId())) {
            throw new BadRequestException("Cannot operate on self");
        }
        switch (request.getAction()) {
            case delete_user:
                dataConnector.deleteUser(id);
                return dataConnector.getUserGamesByUser(id, true);
            case delete_game:
                dataConnector.deleteState(id, null);
                for (Player player : dataConnector.getBoard(id).getPlayers()) {
                    dataConnector.deleteUserGame(UserGame.generateId(id, player.getId()));
                }
                dataConnector.deleteBoard(id);
                break;
            case end_turn:
                moveApiHelper.play(OutOfTurnApi.ADMIN, null, id, null, miscMoveHelper::endTurn, Phase.gameplay);
                break;
            case get_state:
                return dataConnector.getState(id, null);
            case set_state:
                // This is a yolo update without any validations. If u break something, that's ur problem.
                dataConnector.updateState(request.getState());
                break;
            case add_admin:
            case remove_admin:
                User u = dataConnector.getUser(id);
                boolean isAdmin = request.getAction() == AdminAction.add_admin;
                if (u.getRoles() == null) {
                    u.setRoles(new HashSet<>());
                }
                if (isAdmin ? u.getRoles().add(Role.ADMIN) : u.getRoles().remove(Role.ADMIN)) {
                    if (u.getRoles().isEmpty()) {
                        u.setRoles(null);
                    }
                    dataConnector.updateUser(u);
                }
                break;
            default:
                throw new CatanException("Unexpected value: " + request.getAction(), Status.NOT_IMPLEMENTED);
        }
        return null;
    }

    private void handleNewUser(User user) {
        if (INITIAL_ADMIN_USER.equals(user.getId())) {
            user.setRoles(Set.of(Role.ADMIN));
        }
    }
}
