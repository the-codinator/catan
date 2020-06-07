/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.impl.game;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.House;
import org.codi.catan.model.game.HouseType;
import org.codi.catan.model.game.Phase;
import org.codi.catan.model.game.Road;
import org.codi.catan.model.game.State;
import org.codi.catan.model.request.SetupMoveRequest;

@Singleton
public class SetupMoveHelper {

    private final GraphHelper graphHelper;
    private final GameUtility gameUtility;
    private final BuildMoveHelper buildMoveHelper;
    private final MiscMoveHelper miscMoveHelper;

    @Inject
    public SetupMoveHelper(GraphHelper graphHelper, GameUtility gameUtility, BuildMoveHelper buildMoveHelper,
        MiscMoveHelper miscMoveHelper) {
        this.graphHelper = graphHelper;
        this.gameUtility = gameUtility;
        this.buildMoveHelper = buildMoveHelper;
        this.miscMoveHelper = miscMoveHelper;
    }

    /**
     * Play setup 1 or 2
     */
    public void play(Board board, State state, SetupMoveRequest request) throws CatanException {
        // Validate input
        graphHelper.validateVertex(request.getHouseVertex());
        graphHelper.validateVertex(request.getRoadVertex());
        buildMoveHelper.ensureCanPlaceHouse(state, request.getHouseVertex(), HouseType.settlement);
        buildMoveHelper.ensureCanPlaceRoad(state, request.getHouseVertex(), request.getRoadVertex());
        // Create settlement & road
        Color color = state.getCurrentMove().getColor();
        state.getHouses().put(request.getHouseVertex(), new House(color, HouseType.settlement));
        state.getRoads().add(new Road(color, request.getHouseVertex(), request.getRoadVertex()));
        // Gain resources
        if (state.getPhase() == Phase.setup2) {
            for (int hex : graphHelper.getConnectedHexListForVertex(request.getHouseVertex())) {
                gameUtility.transferResources(state, null, color, board.getTiles()[hex].getResource());
            }
        }
        // Auto end turn
        miscMoveHelper.endTurn(board, state);
    }
}
