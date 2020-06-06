/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.impl.game;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.House;
import org.codi.catan.model.game.HouseType;
import org.codi.catan.model.game.Phase;
import org.codi.catan.model.game.Resource;
import org.codi.catan.model.game.Road;
import org.codi.catan.model.game.State;
import org.codi.catan.model.request.SetupMoveRequest;

@Singleton
public class SetupMoveHelper {

    private final GraphHelper graphHelper;
    private final GameUtility gameUtility;
    private final MiscMoveHelper miscMoveHelper;

    @Inject
    public SetupMoveHelper(GraphHelper graphHelper, GameUtility gameUtility, MiscMoveHelper miscMoveHelper) {
        this.graphHelper = graphHelper;
        this.gameUtility = gameUtility;
        this.miscMoveHelper = miscMoveHelper;
    }

    /**
     * Play setup 1 or 2
     */
    public void play(Board board, State state, SetupMoveRequest request) throws CatanException {
        // Validate input
        graphHelper.validateVertex(request.getHouseVertex());
        graphHelper.validateVertex(request.getRoadVertex());
        if (!graphHelper.isAdjacentVertices(request.getHouseVertex(), request.getRoadVertex())) {
            throw new CatanException("Vertices are not adjacent", Status.BAD_REQUEST);
        }
        // Validate no house on vertex
        if (state.getHouses().containsKey(request.getHouseVertex())) {
            throw new CatanException("Cannot create buildings on other buildings", Status.BAD_REQUEST);
        }
        // Validate no adjacent house
        for (int vertex : graphHelper.getAdjacentVertexListForVertex(request.getHouseVertex())) {
            if (state.getHouses().containsKey(vertex)) {
                throw new CatanException("Cannot create buildings on adjacent vertices", Status.BAD_REQUEST);
            }
        }
        // Create settlement
        Color color = state.getCurrentMove().getColor();
        state.getHouses().put(request.getHouseVertex(), new House(color, HouseType.settlement));
        // Create road
        state.getRoads().add(new Road(color, request.getHouseVertex(), request.getRoadVertex()));
        // Gain resources
        if (state.getPhase() == Phase.setup2) {
            for (int hex : graphHelper.getConnectedHexListForVertex(request.getHouseVertex())) {
                Resource resource = board.getTiles()[hex].getResource();
                if (resource != null) {
                    gameUtility.transferResourcesWithBank(state, resource, 1);
                }
            }
        }
        // Auto end turn
        miscMoveHelper.endTurn(board, state);
    }
}
