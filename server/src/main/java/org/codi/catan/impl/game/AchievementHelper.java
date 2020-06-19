/*
 * @author the-codinator
 * created on 2020/6/7
 */

package org.codi.catan.impl.game;

import static org.codi.catan.util.Constants.MAX_ROADS_PER_PLAYER;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.AchievementType;
import org.codi.catan.model.game.AchievementValue;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.DevCard;
import org.codi.catan.model.game.Road;
import org.codi.catan.model.game.State;

@Singleton
public class AchievementHelper {

    private final GraphHelper graphHelper;

    @Inject
    public AchievementHelper(GraphHelper graphHelper) {
        this.graphHelper = graphHelper;
    }

    /**
     * Check for any changes to the largest army achievement
     */
    public void handleLargestArmy(State state) {
        Color color = state.getCurrentMove().getColor();
        AchievementValue achievement = state.getAchievements().get(AchievementType.largest_army);
        if (achievement.getColor() == color) {
            return;
        }
        int knights = (int) state.getPlayedDevCards().get(color).stream().filter(DevCard.knight::equals).count();
        if (knights > achievement.getCount()) {
            achievement.setColor(color);
            achievement.setCount(knights);
        }
    }

    /**
     * Check for any changes to the longest road achievement
     *
     * Note: Even if current player holds the achievement, we may need to update the count
     */
    public void handleLongestRoad(State state) throws CatanException {
        /* Note: Calculating this in a generic graph is an NP-Hard problem. We're going ahead with a brute force
         * approach since we have tight upper bound on the graph size.
         * Algorithm: Apply DFS with backtracking from every vertex where we have an edge and search longest path
         * starting from there
         */
        Color color = state.getCurrentMove().getColor();
        AchievementValue achievement = state.getAchievements().get(AchievementType.longest_road);
        if (achievement.getCount() == MAX_ROADS_PER_PLAYER) {
            return;
        }
        int count = 0;
        int size = state.getRoads().size();
        for (Road road : state.getRoads()) {
            size--;
            if (road.getColor() == color) {
                count++;
            } else if (size == 0) {
                // Invoked with bad state context
                throw new CatanException("Last created road does not belong to current player");
            }
        }
        if (count <= achievement.getCount()) {
            return;
        }
        Road[] roads = new Road[count];
        boolean[] vertices = new boolean[graphHelper.getVertexCount()]; // List of vertices that we have edges for
        int i = 0;
        for (Road road : state.getRoads()) {
            if (road.getColor() == color) {
                roads[i++] = road;
                vertices[road.getVertex1()] = vertices[road.getVertex2()] = true;
            }
        }
        int max = 0;
        boolean[] visited = new boolean[count];
        for (i = 0; i < vertices.length; i++) {
            if (vertices[i]) {
                // DFS for all possible starting vertices
                max = dfs(i, roads, visited, 0, max);
            }
        }
        if (max > achievement.getCount()) {
            achievement.setColor(color);
            achievement.setCount(max);
        }
    }

    private int dfs(int vertex, Road[] roads, boolean[] visited, int curr, int max) throws CatanException {
        if (curr > MAX_ROADS_PER_PLAYER) {
            // Safe guard to avoid potential infinite loops due to 4am coding
            throw new CatanException("DFS depth exceeded max");
        }
        for (int i = 0; i < roads.length; i++) {
            if (!visited[i]) {
                int v = getOtherVertex(roads[i], vertex);
                if (v != -1) {
                    // Visit
                    visited[i] = true;
                    // Recursion based DFS
                    max = dfs(v, roads, visited, curr + 1, max);
                    // Backtrack
                    visited[i] = false;
                }
            }
        }
        return Math.max(curr, max);
    }

    private int getOtherVertex(Road road, int vertex) {
        return vertex == road.getVertex1() ? road.getVertex2() : vertex == road.getVertex2() ? road.getVertex1() : -1;
    }
}
