package GraphXings.Algorithms;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameMove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class RandomChoicePlayer implements Player {

    /**
     * The name of the random player.
     */
    private String name;
    private int maxPoints;
    private CrossingCalculatorAlgorithm crossingCalculator;

    /**
     * Creates a random player with the assigned name.
     * 
     * @param name
     */
    public RandomChoicePlayer(String name, int maxPointsPerMove, CrossingCalculatorAlgorithm crossingCalculator) {
        this.name = name;
        this.maxPoints = maxPointsPerMove;
        this.crossingCalculator = crossingCalculator;
    }

    public GameMove

            maximizeCrossings(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates, List<GameMove> gameMoves,
                    int[][] usedCoordinates, HashSet<Vertex> placedVertices, int width, int height) {
        return betterMove(g, usedCoordinates, vertexCoordinates, placedVertices, width, height, true);
    }

    @Override
    public GameMove minimizeCrossings(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates, List<GameMove> gameMoves,
            int[][] usedCoordinates, HashSet<Vertex> placedVertices, int width, int height) {
        return betterMove(g, usedCoordinates, vertexCoordinates, placedVertices, width, height, false);
    }

    @Override
    public void initializeNextRound() {

    }

    private Coordinate getRandomUnusedCoord(int[][] usedCoordinates, Random r, int width, int height) {

        var c = new Coordinate(0, 0);
        do {
            c = new Coordinate(r.nextInt(width), r.nextInt(height));
        } while (usedCoordinates[c.getX()][c.getY()] != 0);
        return c;

    }

    private int[][] copyUsedCoordinates(int[][] usedCoordinates) {
        int[][] copy = new int[usedCoordinates.length][usedCoordinates[0].length];
        for (int i = 0; i < usedCoordinates.length; i++) {
            for (int j = 0; j < usedCoordinates[0].length; j++) {
                copy[i][j] = usedCoordinates[i][j];
            }
        }
        return copy;
    }

    private HashMap<Vertex, Coordinate> copyVertexCoordinates(HashMap<Vertex, Coordinate> vertexCoordinates) {
        HashMap<Vertex, Coordinate> copy = new HashMap<>();
        for (Vertex v : vertexCoordinates.keySet()) {
            copy.put(v, vertexCoordinates.get(v));
        }
        return copy;
    }

    /**
     * Computes a random valid move.
     * 
     * @param g                 The graph.
     * @param vertexCoordinates The used coordinates for each vertex.
     * @param placedVertices    The already placed vertices.
     * @param width             The width of the game board.
     * @param height            The height of the game board.
     * @return A random valid move.
     */
    private GameMove betterMove(Graph g, int[][] usedCoordinates, HashMap<Vertex, Coordinate> vertexCoordinates,
            HashSet<Vertex> placedVertices,
            int width,
            int height, boolean findMax) {
        Random r = new Random();
        int stillToBePlaced = g.getN() - placedVertices.size();
        int next = r.nextInt(stillToBePlaced);
        int skipped = 0;
        Vertex v = null;
        for (Vertex u : g.getVertices()) {
            if (!placedVertices.contains(u)) {
                if (skipped < next) {
                    skipped++;
                    continue;
                }
                v = u;
                break;
            }
        }

        int maxAvailableCoords = (width * height) - placedVertices.size();
        var newUsedCoords = copyUsedCoordinates(usedCoordinates);
        var randomCoords = new ArrayList<Coordinate>();
        int numPoints = Math.min(maxAvailableCoords, maxPoints);
        for (var i = 0; i < numPoints; i++) {
            var newCoord = getRandomUnusedCoord(newUsedCoords, r, width, height);
            newUsedCoords[newCoord.getX()][newCoord.getY()] = 1;
            randomCoords.add(newCoord);
        }
        if (findMax) {
            var c = getBestOfPlacement(randomCoords, usedCoordinates, r, g, v, vertexCoordinates, width, height);
            return new GameMove(v, c);
        } else {
            var c = getWorstOfPlacement(randomCoords, usedCoordinates, r, g, v, vertexCoordinates, width, height);
            return new GameMove(v, c);

        }

    }

    private Coordinate getBestOfPlacement(ArrayList<Coordinate> possibleCoordinates,
            int[][] usedCoordinates, Random r, Graph g,
            Vertex u, HashMap<Vertex, Coordinate> vertexCoordinates, int width, int height) {

        Coordinate coordinateWithMaxCross = possibleCoordinates.get(0);
        int max = 0;

        var newVertexCoordinates = copyVertexCoordinates(vertexCoordinates);
        var newUsedCoordinates = copyUsedCoordinates(usedCoordinates);
        // place remaining verticies randomly
        for (var vertex : g.getVertices()) {
            if (!newVertexCoordinates.containsKey(vertex)) {
                var randomCoord = getRandomUnusedCoord(newUsedCoordinates, r, width, height);
                newUsedCoordinates[randomCoord.getX()][randomCoord.getY()] = 1;
                newVertexCoordinates.put(vertex, randomCoord);
            }
        }
        for (Coordinate c : possibleCoordinates) {
            // place vertex
            newVertexCoordinates.put(u, c);

            int currentCrossingNum = crossingCalculator.computeCrossingNumber(g, newVertexCoordinates);

            if (currentCrossingNum >= max) {
                max = currentCrossingNum;
                coordinateWithMaxCross = c;
            }
        }

        return coordinateWithMaxCross;
    }

    private Coordinate getWorstOfPlacement(ArrayList<Coordinate> possibleCoordinates,
            int[][] usedCoordinates, Random r, Graph g,
            Vertex u, HashMap<Vertex, Coordinate> vertexCoordinates, int width, int height) {

        Coordinate coordinateWithMinCross = possibleCoordinates.get(0);
        int min = 1000000000;

        var newVertexCoordinates = copyVertexCoordinates(vertexCoordinates);
        var newUsedCoordinates = copyUsedCoordinates(usedCoordinates);
        // place remaining verticies randomly
        for (var vertex : g.getVertices()) {
            if (!newVertexCoordinates.containsKey(vertex)) {
                var randomCoord = getRandomUnusedCoord(newUsedCoordinates, r, width, height);
                newUsedCoordinates[randomCoord.getX()][randomCoord.getY()] = 1;
                newVertexCoordinates.put(vertex, randomCoord);
            }
        }
        for (Coordinate c : possibleCoordinates) {
            // place vertex
            newVertexCoordinates.put(u, c);

            int currentCrossingNum = crossingCalculator.computeCrossingNumber(g, newVertexCoordinates);
            if (currentCrossingNum < min) {
                min = currentCrossingNum;
                coordinateWithMinCross = c;
            }
        }

        return coordinateWithMinCross;
    }

    @Override
    public String getName() {
        return name;
    }

}
