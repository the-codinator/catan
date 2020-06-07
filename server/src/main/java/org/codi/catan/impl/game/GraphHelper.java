/*
 * @author the-codinator
 * created on 2020/5/25
 */

package org.codi.catan.impl.game;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Arrays;
import java.util.Collection;
import javax.inject.Singleton;
import org.codi.catan.core.BadRequestException;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Resource;

@Singleton
public class GraphHelper {

    public void validateVertex(int vertex) throws CatanException {
        if (vertex < 0 || vertex >= vertexToAdjacentVertexMatrix.length) {
            throw new BadRequestException("Invalid Vertex Id");
        }
    }

    public void validateHex(int hex) throws CatanException {
        if (hex < 0 || hex >= hexToConnectedVertexMatrix.length) {
            throw new BadRequestException("Invalid Hex Id");
        }
    }

    /**
     * Calculates the normalized vertex id for a {@param port} vertex, -1 if not a port
     */
    public int normalizePort(int port) {
        return find(portVertexList, port) != -1 || find(portVertexList, --port) != -1 ? port : -1;
    }

    public int normalizeAndValidatePort(int port) throws CatanException {
        int normalizedPort = normalizePort(port);
        if (normalizedPort == -1) {
            throw new BadRequestException("Invalid Port Id - " + port);
        }
        return normalizedPort;
    }

    /**
     * Get vertices adjacent to {@param vertex}
     */
    public int[] getAdjacentVertexListForVertex(int vertex) {
        return vertex < 0 || vertex >= vertexToAdjacentVertexMatrix.length ? null
            : vertexToAdjacentVertexMatrix[vertex].clone();
    }

    public boolean isAdjacentVertices(int a, int b) {
        return a >= 0 && a < vertexToAdjacentVertexMatrix.length && find(vertexToAdjacentVertexMatrix[a], b) != -1;
    }

    public int[] getConnectedHexListForVertex(int vertex) {
        return vertex < 0 || vertex >= vertexToConnectedHexMatrix.length ? null
            : vertexToConnectedHexMatrix[vertex].clone();
    }

    public int[] getVerticesAroundHex(int hex) {
        return hex < 0 || hex >= hexToConnectedVertexMatrix.length ? null : hexToConnectedVertexMatrix[hex].clone();
    }

    public int getPortCount() {
        return portVertexList.length;
    }

    public int[] getDiceRollCount() {
        return diceRollCount.clone();
    }

    // --------------------------------------------------------------------------------
    // ---------------- Don't look at / touch anything below this line ----------------
    // --------------------------------------------------------------------------------

    private static final int VERTEX_COUNT = 54;
    private static final int VERTEX_HEX_1_COUNT = 18;
    private static final int VERTEX_HEX_2_COUNT = 12;
    private static final int OUTER_VERTEX_COUNT = VERTEX_HEX_1_COUNT + VERTEX_HEX_2_COUNT;
    private static final int VERTEX_HEX_3_COUNT = VERTEX_COUNT - OUTER_VERTEX_COUNT;
    private static final int EDGE_COUNT = 72;
    private static final int HEX_COUNT = 19;
    private static final int PORT_COUNT = 9;
    private static final int TOTAL_DICE_ROLL_COUNT = 13; // 2 dice * 6 per dice + rolling a 0 (for desert)

    private static final int MIN_ADJ_VERTEX = 2;
    private static final int MAX_ADJ_VERTEX = 3;
    private static final int MIN_ADJ_HEX = 1;
    private static final int MAX_ADJ_HEX = 3;
    private static final int MIN_DIST_BETWEEN_PORT = 3;
    private static final int MAX_DIST_BETWEEN_PORT = 4;
    private static final int VERTEX_COUNT_PER_HEX = 6;
    private static final int MIN_TILE_RESOURCE_COUNT = 3;
    private static final int MAX_TILE_RESOURCE_COUNT = 4;
    private static final int MIN_DICE_ROLL_COUNT = 0;
    private static final int MAX_DICE_ROLL_COUNT = 2;

    private static final int[][] vertexToAdjacentVertexMatrix = {{1, 29}, {0, 2, 31}, {1, 3}, {2, 4}, {3, 5, 32},
        {4, 6}, {5, 7, 34}, {6, 8}, {7, 9}, {8, 10, 35}, {9, 11}, {10, 12, 37}, {11, 13}, {12, 14}, {13, 15, 38},
        {14, 16}, {15, 17, 40}, {16, 18}, {17, 19}, {18, 20, 41}, {19, 21}, {20, 22, 43}, {21, 23}, {22, 24},
        {23, 25, 44}, {24, 26}, {25, 27, 46}, {26, 28}, {27, 29}, {0, 28, 47}, {31, 47, 48}, {1, 30, 32}, {4, 31, 33},
        {32, 34, 49}, {6, 33, 35}, {9, 34, 36}, {35, 37, 50}, {11, 36, 38}, {14, 37, 39}, {38, 40, 51}, {16, 39, 41},
        {19, 40, 42}, {41, 43, 52}, {21, 42, 44}, {24, 43, 45}, {44, 46, 53}, {26, 45, 47}, {29, 30, 46}, {30, 49, 53},
        {33, 48, 50}, {36, 49, 51}, {39, 50, 52}, {42, 51, 53}, {45, 48, 52}};

    private static final int[][] hexToConnectedVertexMatrix = {{0, 1, 29, 30, 31, 47}, {1, 2, 3, 4, 31, 32},
        {4, 5, 6, 32, 33, 34}, {6, 7, 8, 9, 34, 35}, {9, 10, 11, 35, 36, 37}, {11, 12, 13, 14, 37, 38},
        {14, 15, 16, 38, 39, 40}, {16, 17, 18, 19, 40, 41}, {19, 20, 21, 41, 42, 43}, {21, 22, 23, 24, 43, 44},
        {24, 25, 26, 44, 45, 46}, {26, 27, 28, 29, 46, 47}, {30, 31, 32, 33, 48, 49}, {33, 34, 35, 36, 49, 50},
        {36, 37, 38, 39, 50, 51}, {39, 40, 41, 42, 51, 52}, {42, 43, 44, 45, 52, 53}, {30, 45, 46, 47, 48, 53},
        {48, 49, 50, 51, 52, 53}};

    private static final int[][] vertexToConnectedHexMatrix = new int[VERTEX_COUNT][];

    private static final int[] portVertexList = {0, 4, 7, 10, 14, 17, 20, 24, 27};

    private static final int[] diceRollCount = {1, 0, 1, 2, 2, 2, 2, 0, 2, 2, 2, 2, 1}; // Desert -> 0

    static {
        try {
            shallowValidateVertexAdjacencyMatrix();
            shallowValidateVerticesAroundHex();
            generateVertexHexMapping();
            shallowValidatePorts();
            validateResourceTileCount();
            shallowValidateDiceRollCount();
        } catch (CatanException e) {
            throw new RuntimeException("Bad board graph data", e);
        }
    }

    private static void ensure(boolean b, String property) throws CatanException {
        if (!b) {
            throw new CatanException("Invalid " + property);
        }
    }

    private static int find(int[] arr, int val) {
        for (int i = 0; i < arr.length; i++) {
            if (val == arr[i]) {
                return i;
            }
        }
        return -1;
    }

    private static void shallowValidateVertexAdjacencyMatrix() throws CatanException {
        ensure(vertexToAdjacentVertexMatrix.length == VERTEX_COUNT, "vertex count");
        int count = 0;
        for (int i = 0; i < vertexToAdjacentVertexMatrix.length; i++) {
            int[] neighbors = vertexToAdjacentVertexMatrix[i];
            ensure(neighbors.length >= MIN_ADJ_VERTEX, "min neighbor count");
            ensure(neighbors.length <= MAX_ADJ_VERTEX, "max neighbor count");
            int prev = -1;
            for (int neighbor : neighbors) {
                ensure(neighbor > prev, "vertex ordering");
                prev = neighbor;
                ensure(neighbor >= 0 && neighbor < VERTEX_COUNT, "vertex id");
                ensure(find(vertexToAdjacentVertexMatrix[neighbor], i) != -1, "2 way adj vertex binding");
                count++;
            }
        }
        ensure(count == 2 * EDGE_COUNT, "adj vertex count");
    }

    private static void shallowValidateVerticesAroundHex() throws CatanException {
        ensure(hexToConnectedVertexMatrix.length == HEX_COUNT, "number of hexes");
        int[] count = new int[VERTEX_COUNT];
        for (int[] vertices : hexToConnectedVertexMatrix) {
            ensure(vertices.length == VERTEX_COUNT_PER_HEX, "hex size");
            int prev = -1;
            for (int vertex : vertices) {
                ensure(vertex > prev, "vertex ordering");
                prev = vertex;
                ensure(vertex >= 0 && vertex < VERTEX_COUNT, "vertex id");
                count[vertex]++;
            }
        }
        int[] countCount = new int[MAX_ADJ_HEX + 1];
        for (int c : count) {
            ensure(c >= MIN_ADJ_HEX && c <= MAX_ADJ_HEX, "adj hex count");
            countCount[c]++;
        }
        ensure(countCount[0] == 0, "vertex w/o hex");
        ensure(countCount[1] == VERTEX_HEX_1_COUNT, "1 hex vertex count");
        ensure(countCount[2] == VERTEX_HEX_2_COUNT, "2 hex vertex count");
        ensure(countCount[3] == VERTEX_HEX_3_COUNT, "3 hex vertex count");
    }

    private static void generateVertexHexMapping() throws CatanException {
        Multimap<Integer, Integer> matrix = HashMultimap.create(vertexToConnectedHexMatrix.length, MAX_ADJ_HEX);
        for (int i = 0; i < hexToConnectedVertexMatrix.length; i++) {
            for (int vertex : hexToConnectedVertexMatrix[i]) {
                matrix.put(vertex, i);
            }
        }
        for (int i = 0; i < vertexToConnectedHexMatrix.length; i++) {
            Collection<Integer> hexes = matrix.get(i);
            vertexToConnectedHexMatrix[i] = new int[hexes.size()];
            int j = 0;
            for (int hex : hexes) {
                vertexToConnectedHexMatrix[i][j++] = hex;
            }
            Arrays.sort(vertexToConnectedHexMatrix[i]);
            ensure(vertexToConnectedHexMatrix[i].length > 0, "min hex count per vertex");
            ensure(vertexToConnectedHexMatrix[i].length <= MAX_ADJ_HEX, "max hex count per vertex");
        }
    }

    private static void shallowValidatePorts() throws CatanException {
        ensure(portVertexList.length == PORT_COUNT, "port count");
        int prev = -MIN_DIST_BETWEEN_PORT;
        for (int port : portVertexList) {
            ensure(port >= prev + MIN_DIST_BETWEEN_PORT, "inter-port distance/port ordering");
            ensure(port <= prev + MAX_DIST_BETWEEN_PORT, "inter-port distance/port ordering");
            prev = port;
            ensure(port >= 0 && port < OUTER_VERTEX_COUNT, "port vertex id");
        }
    }

    private static void validateResourceTileCount() throws CatanException {
        int count = 0;
        for (Resource resource : Resource.values()) {
            ensure(resource.getTileCount() >= MIN_TILE_RESOURCE_COUNT, "min tiles for resource");
            ensure(resource.getTileCount() <= MAX_TILE_RESOURCE_COUNT, "max tiles for resource");
            count += resource.getTileCount();
        }
        ensure(count + 1 /* Desert */ == HEX_COUNT, "tile count");
    }

    private static void shallowValidateDiceRollCount() throws CatanException {
        int count = 0;
        for (int drc : diceRollCount) {
            ensure(drc >= MIN_DICE_ROLL_COUNT, "min dice roll count");
            ensure(drc <= MAX_DICE_ROLL_COUNT, "max dice roll count");
            count += drc;
        }
        ensure(count == hexToConnectedVertexMatrix.length, "tile roll counts");
        ensure(diceRollCount[0] == 1, "desert roll count");
        ensure(diceRollCount[1] == 0, "1-roll count");
    }
}
