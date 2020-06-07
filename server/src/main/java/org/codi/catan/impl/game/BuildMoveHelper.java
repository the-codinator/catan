/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.impl.game;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.House;
import org.codi.catan.model.game.HouseType;
import org.codi.catan.model.game.Resource;
import org.codi.catan.model.game.Road;
import org.codi.catan.model.game.State;
import org.codi.catan.model.request.HouseRequest;
import org.codi.catan.model.request.RoadRequest;

@Singleton
public class BuildMoveHelper {

    private final GraphHelper graphHelper;
    private final GameUtility gameUtility;
    private final AchievementHelper achievementHelper;

    @Inject
    public BuildMoveHelper(GraphHelper graphHelper, GameUtility gameUtility, AchievementHelper achievementHelper) {
        this.graphHelper = graphHelper;
        this.gameUtility = gameUtility;
        this.achievementHelper = achievementHelper;
    }

    /**
     * Build a road
     */
    public void road(State state, RoadRequest request) throws CatanException {
        ensureCanPlaceRoad(state, request.getVertex1(), request.getVertex2());
        Color color = state.getCurrentMove().getColor();
        gameUtility.transferResources(state, color, null, Resource.wood, Resource.brick);
        Road road = new Road(color, request.getVertex1(), request.getVertex2());
        state.getRoads().add(road);
        achievementHelper.handleLongestRoad(state, road);
    }

    /**
     * Check if placing a road connecting the vertices is a legal move or not
     * We don't need to check anything related to houses since every house MUST have at least 1 connected road
     */
    public void ensureCanPlaceRoad(State state, int vertex1, int vertex2) throws CatanException {
        if (!graphHelper.isAdjacentVertices(vertex1, vertex2)) {
            throw new CatanException("Cannot place road between non-adjacent vertices", Status.BAD_REQUEST);
        }
        Color color = state.getCurrentMove().getColor();
        boolean valid = state.getPhase().isSetupPhase(); // in setup phase, we can have house without connected road
        for (Road road : state.getRoads()) {
            // Check existing road
            if (road.getVertex1() == vertex1 && road.getVertex2() == vertex2
                || road.getVertex1() == vertex2 && road.getVertex2() == vertex1) {
                valid = false;
                break;
            }
            // Check connecting road
            if (!valid && road.getColor() == color && (road.getVertex1() == vertex1 || road.getVertex1() == vertex2
                || road.getVertex2() == vertex1 || road.getVertex2() == vertex2)) {
                valid = true;
                // Don't break, need to test all roads for "existing road"
            }
        }
        if (!valid) {
            throw new CatanException("Invalid location for road", Status.BAD_REQUEST);
        }
    }

    /**
     * Build a house
     */
    public void house(State state, HouseRequest request) throws CatanException {
        HouseType type = state.getHouses().containsKey(request.getVertex()) ? HouseType.city : HouseType.settlement;
        ensureCanPlaceHouse(state, request.getVertex(), type);
        Color color = state.getCurrentMove().getColor();
        switch (type) {
            case settlement:
                gameUtility.transferResources(state, color, null, Resource.wood, Resource.brick, Resource.hay,
                    Resource.sheep);
                state.getHouses().put(request.getVertex(), new House(color, type));
                break;
            case city:
                gameUtility.transferResources(state, color, null, Resource.hay, 2);
                gameUtility.transferResources(state, color, null, Resource.rock, 3);
                state.getHouses().get(request.getVertex()).setType(HouseType.city);
                break;
            default:
        }
    }

    /**
     * Check if placing a house is a legal move or not
     * We don't need to check anything related to houses since every house MUST have at least 1 connected road
     *
     * @param type Type of house being created
     */
    public void ensureCanPlaceHouse(State state, int vertex, HouseType type) throws CatanException {
        switch (type) {
            case settlement:
                // Validate no house on vertex
                if (state.getHouses().containsKey(vertex)) {
                    throw new CatanException("Cannot create settlement on existing building", Status.BAD_REQUEST);
                }
                // Validate no adjacent house
                for (int adjVertex : graphHelper.getAdjacentVertexListForVertex(vertex)) {
                    if (state.getHouses().containsKey(adjVertex)) {
                        throw new CatanException("Cannot create settlement adjacent to other buildings",
                            Status.BAD_REQUEST);
                    }
                }
                break;
            case city:
                House house = state.getHouses().get(vertex);
                if (house == null || house.getType() != HouseType.settlement
                    || house.getColor() != state.getCurrentMove().getColor()) {
                    throw new CatanException("Cannot upgrade to city without own settlement", Status.BAD_REQUEST);
                }
                break;
            default:
        }
    }
}
