package GraphXings.Algorithms;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Segment;
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
    private int lastCrossingCount = 0;

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

    public GameMove maximizeCrossings(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates, List<GameMove> gameMoves,
            int[][] usedCoordinates, HashSet<Vertex> placedVertices, int width, int height) {
        final long timeStart = System.currentTimeMillis();
        GameMove betterMove = betterMove(g, usedCoordinates, vertexCoordinates, placedVertices, width, height, true);
        final long timeEnd = System.currentTimeMillis();
        System.out.println("betterMove(): " + (timeEnd - timeStart) + " Millisek. ("
                + crossingCalculator.getClass().toString() + ")");
        return betterMove;
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

    private Coordinate getInterval(int[][] usedCoordinates, int width, int height, Graph g, double intervalXStartValue,
            Random r) {

        double distance = Math.abs(intervalXStartValue - (1 - intervalXStartValue));

        var c = new Coordinate(0, 0);
        int sizeOfField = (int) ((distance * width) * (distance * height));
        if (sizeOfField < g.getN()) {
            long intervalLength = Math.round(Math.sqrt(g.getN()));
            c = new Coordinate((int) ((height / 2) - (intervalLength / 2)),
                    (int) ((height / 2) + (intervalLength / 2)));
        }
    }

    private Coordinate getNotRandomUnusedCoord(int[][] usedCoordinates, Random r, int width, int height,
            boolean findMax) {
        var c = new Coordinate(0, 0);
        double intervalXStartValue = 0.4;

        // int sizeOfField = (int) ((distance * width) * (distance * height));
        do {
            if (findMax) {
                c = new Coordinate(r.nextInt((int) (0.9 * width), width),
                        r.nextInt((int) (0.9 * height), height));
            } else {
                c = new Coordinate(r.nextInt((int) (0.4 * width), (int) (0.6 * width)),
                        r.nextInt((int) (0.4 * height), (int) (0.6 * height)));
            }
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
        Vertex v = null;

        for (Vertex u : g.getVertices()) {
            if (!placedVertices.contains(u)) {
                v = u;
                break;
            }
        }

        int maxAvailableCoords = (width * height) - placedVertices.size();
        var newUsedCoords = copyUsedCoordinates(usedCoordinates);
        var randomCoords = new ArrayList<Coordinate>();
        int numPoints = Math.min(maxAvailableCoords, maxPoints);
        lastCrossingCount = crossingCalculator.computeCrossingNumber(g, vertexCoordinates);
        for (var i = 0; i < numPoints; i++) {
            var newCoord = getNotRandomUnusedCoord(newUsedCoords, r, width, height, findMax);
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

    private int calculateNewEdgeCrossings(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates,
            Vertex newPlacedVertex) {
        int crossingNumber = 0;
        var adjacentEdges = g.getIncidentEdges(newPlacedVertex);
        for (Edge e1 : adjacentEdges) {

            for (Edge e2 : g.getEdges()) {
                if (!e1.equals(e2)) {
                    if (!e1.isAdjacent(e2)) {

                        var startCoord1 = vertexCoordinates.get(e1.getS());
                        var endCoord1 = vertexCoordinates.get(e1.getT());
                        var startCoord2 = vertexCoordinates.get(e2.getS());
                        var endCoord2 = vertexCoordinates.get(e2.getT());

                        if (startCoord1 == null || startCoord2 == null || endCoord1 == null || endCoord2 == null) {
                            continue;
                        }

                        Segment s1 = new Segment(startCoord1, endCoord1);
                        Segment s2 = new Segment(startCoord2, endCoord2);
                        if (BasicCrossingCalculatorAlgorithm.reportIntersection(s1, s2) != null) {
                            crossingNumber++;
                        }
                    }
                }
            }
        }
        return crossingNumber / 2;
    }

    private Coordinate getBestOfPlacement(ArrayList<Coordinate> possibleCoordinates,
            int[][] oldUsedCoordinates, Random r, Graph g,
            Vertex vertexToBePlaced, HashMap<Vertex, Coordinate> oldVertexCoordinates, int width, int height) {

        Coordinate coordinateWithMaxCross = possibleCoordinates.get(0);
        int max = 0;
        var newVertexCoordinates = copyVertexCoordinates(oldVertexCoordinates);

        for (Coordinate c : possibleCoordinates) {
            // place vertex
            newVertexCoordinates.put(vertexToBePlaced, c);
            int currentCrossingNum = calculateNewEdgeCrossings(g, newVertexCoordinates, vertexToBePlaced);

            if (currentCrossingNum >= max) {
                max = currentCrossingNum;
                coordinateWithMaxCross = c;
            }
        }
        System.out.println("Vertex found with " + max + " crossings if added");
        lastCrossingCount = max;
        return coordinateWithMaxCross;
    }

    private Coordinate getWorstOfPlacement(ArrayList<Coordinate> possibleCoordinates,
            int[][] usedCoordinates, Random r, Graph g,
            Vertex vertexToBePlaced, HashMap<Vertex, Coordinate> vertexCoordinates, int width, int height) {

        Coordinate coordinateWithMinCross = possibleCoordinates.get(0);
        int min = 1000000000;
        var newVertexCoordinates = copyVertexCoordinates(vertexCoordinates);

        for (Coordinate c : possibleCoordinates) {
            // place vertex
            newVertexCoordinates.put(vertexToBePlaced, c);

            int currentCrossingNum = calculateNewEdgeCrossings(g, newVertexCoordinates, vertexToBePlaced);
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
