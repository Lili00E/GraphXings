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

public class BetterPlayer implements Player {

    /**
     * The name of the random player.
     */
    private String name;
    RandomPlayer rp = new RandomPlayer("Lili");

    /**
     * Creates a random player with the assigned name.
     * 
     * @param name
     */
    public BetterPlayer(String name) {
        this.name = name;
    }

    @Override
    public GameMove maximizeCrossings(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates, List<GameMove> gameMoves,
            int[][] usedCoordinates, HashSet<Vertex> placedVertices, int width, int height) {
        return betterMove(g, usedCoordinates, vertexCoordinates, placedVertices, width, height);
    }

    @Override
    public GameMove minimizeCrossings(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates, List<GameMove> gameMoves,
            int[][] usedCoordinates, HashSet<Vertex> placedVertices, int width, int height) {
        return rp.randomMove(g, usedCoordinates, placedVertices, width, height);
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
            int height) {
        Random r = new Random();
        ArrayList<Vertex> stillToBePlaced = new ArrayList<>();
        ArrayList<Coordinate> possibleCoordinates = new ArrayList<>();
        ArrayList<HashMap<Integer, Coordinate>> ListOfBestNodeOutcomes = new ArrayList<>();

        for (Vertex element : g.getVertices()) {
            if (!placedVertices.contains(element)) {
                stillToBePlaced.add(element);
            }
        }

        if (vertexCoordinates.isEmpty()) {
            return new GameMove(stillToBePlaced.get(0), getRandomUnusedCoord(usedCoordinates, r, width, height));
        }

        for (Vertex toBePlacedVertex : stillToBePlaced) {
            for (Coordinate i : vertexCoordinates.values()) {
                for (Coordinate j : vertexCoordinates.values()) {
                    if (i != null && j != null) {
                        int x = (i.getX() + j.getX()) / 2;
                        int y = (i.getY() + j.getY()) / 2;
                        Coordinate c = new Coordinate(x, y);
                        if (usedCoordinates[x][y] != 0) {
                            continue;
                        }
                        possibleCoordinates.add(c);
                    }
                }
            }
            HashMap<Integer, Coordinate> bestNodeOutcome = getCoordinateWithMaxCross(possibleCoordinates,
                    usedCoordinates, r, g,
                    toBePlacedVertex, vertexCoordinates, width, height);
            ListOfBestNodeOutcomes.add(bestNodeOutcome);
        }
        // Liste mit bestNodeOutcomes und dann max bestimmten
        int maxNumberOfCrossings = Integer.MIN_VALUE; // Initialisieren mit einem minimalen Wert
        Coordinate bestNewCoordinate = new Coordinate(0, 0);
        int indexOfMaxCrossingVertex = 0;

        int currentVertexIndex = 0;
        for (HashMap<Integer, Coordinate> bestOutcome : ListOfBestNodeOutcomes) {
            for (int numberOfCrossings : bestOutcome.keySet()) {
                if (numberOfCrossings > maxNumberOfCrossings) {
                    maxNumberOfCrossings = numberOfCrossings;
                    bestNewCoordinate = bestOutcome.get(maxNumberOfCrossings);
                    indexOfMaxCrossingVertex = currentVertexIndex;
                }
            }
            currentVertexIndex++;
        }

        return new GameMove(stillToBePlaced.get(indexOfMaxCrossingVertex), bestNewCoordinate);

    }

    private HashMap<Integer, Coordinate> getCoordinateWithMaxCross(ArrayList<Coordinate> possibleCoordinates,
            int[][] usedCoordinates, Random r, Graph g,
            Vertex u, HashMap<Vertex, Coordinate> vertexCoordinates, int width, int height) {
        Coordinate CoordinateWithMaxCross = new Coordinate(0, 0);
        int max = 0;

        HashMap<Vertex, Coordinate> newVertexCoordinates = new HashMap<>();
        var newUsedCoordinates = copyUsedCoordinates(usedCoordinates);
        for (Map.Entry<Vertex, Coordinate> e : vertexCoordinates.entrySet()) {
            var newCoordinates = vertexCoordinates.get(e.getKey());
            newVertexCoordinates.put(e.getKey(), newCoordinates);
            newUsedCoordinates[newCoordinates.getX()][newCoordinates.getY()] = 1;
        }
        for (var vertex : g.getVertices()) {
            if (!newVertexCoordinates.containsKey(vertex)) {
                var randomCoord = getRandomUnusedCoord(newUsedCoordinates, r, width, height);
                newUsedCoordinates[randomCoord.getX()][randomCoord.getY()] = 1;
                newVertexCoordinates.put(vertex, randomCoord);
            }
        }

        HashMap<Integer, Coordinate> bestNodeOutcome = new HashMap<>();
        for (Coordinate c : possibleCoordinates) {
            newVertexCoordinates.put(u, c);

            CrossingCalculator currentCc = new CrossingCalculator(g, newVertexCoordinates);
            int currentCrossingNum = currentCc.computeCrossingNumber();
            if (currentCrossingNum >= max) {
                max = currentCrossingNum;
                CoordinateWithMaxCross = c;
            }
        }
        bestNodeOutcome.put(max, CoordinateWithMaxCross);
        return bestNodeOutcome;
    }

    @Override
    public String getName() {
        return name;
    }

}
