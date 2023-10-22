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
        return betterMove(g, vertexCoordinates, placedVertices, width, height);
    }

    @Override
    public GameMove minimizeCrossings(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates, List<GameMove> gameMoves,
            int[][] usedCoordinates, HashSet<Vertex> placedVertices, int width, int height) {
        return rp.randomMove(g, usedCoordinates, placedVertices, width, height);
    }

    @Override
    public void initializeNextRound() {

    }

    /**
     * Computes a random valid move.
     * 
     * @param g               The graph.
     * @param vertexCoordinates The used coordinates for each vertex.
     * @param placedVertices  The already placed vertices.
     * @param width           The width of the game board.
     * @param height          The height of the game board.
     * @return A random valid move.
     */
    private GameMove betterMove(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates, HashSet<Vertex> placedVertices,
                                int width,
                                int height) {

        ArrayList<Vertex> stillToBePlaced = new ArrayList<>();
        ArrayList<Coordinate> possibleCoordinates = new ArrayList<>();
        ArrayList<HashMap<Integer, Coordinate>> ListOfBestNodeOutcomes = new ArrayList<>();

        for (Vertex element : g.getVertices()) {
            if (!placedVertices.contains(element)) {
                stillToBePlaced.add(element);
            }
        }

        for (Vertex toBePlacedVertex : stillToBePlaced) {
            for (Coordinate i : vertexCoordinates.values()) {
                for (Coordinate j : vertexCoordinates.values()) {
                    if (i != null && j != null) {
                        int x = (i.getX() + j.getX()) / 2;
                        int y = (i.getY() + j.getY()) / 2;
                        Coordinate c = new Coordinate(x, y);
                        possibleCoordinates.add(c);
                    }
                }
            }
            HashMap<Integer, Coordinate> bestNodeOutcome = getCoordinateWithMaxCross(possibleCoordinates, g,
                    toBePlacedVertex, vertexCoordinates);
            ListOfBestNodeOutcomes.add(bestNodeOutcome);
        }
        // Liste mit bestNodeOutcomes und dann max bestimmten
        int maxCross = Integer.MIN_VALUE; // Initialisieren mit einem minimalen Wert
        Coordinate bestNewCoordinate = new Coordinate(0,0);
        int vCount = 0;
        int vMax = 0;

        for (HashMap<Integer, Coordinate> bestOutcome : ListOfBestNodeOutcomes) {
            for (int key : bestOutcome.keySet()) {
                int cross = key;                        ;
                if (cross > maxCross) {
                    maxCross = cross;
                    bestNewCoordinate = bestOutcome.get(key);
                    vMax = vCount;
                }
            }
            vCount++;
        }


        return new GameMove(stillToBePlaced.get(vMax), bestNewCoordinate);

    }

    private HashMap<Integer, Coordinate> getCoordinateWithMaxCross(ArrayList<Coordinate> possibleCoordinates, Graph g,
            Vertex u, HashMap<Vertex, Coordinate> vertexCoordinates) {
        Coordinate CoordinateWithMaxCross = new Coordinate(0, 0);
        int max = 0;

        HashMap<Vertex, Coordinate> newVertexCoordinates = new HashMap<>();
        for(Map.Entry<Vertex, Coordinate> e: vertexCoordinates.entrySet()) {
            newVertexCoordinates.put(e.getKey(),vertexCoordinates.get(e.getKey()));
            System.out.println(newVertexCoordinates);
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
