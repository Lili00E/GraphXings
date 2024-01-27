package GraphXings.Gruppe10;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Data.Coordinate;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameState;

import java.util.*;

import static GraphXings.Gruppe10.Util.*;

public class LighthousePlayer implements NewPlayer {
    private Graph g;
    private int width;
    private int height;
    private GameState gs;
    private Random r;
    private int innerLoopStep;
    private int outerLoopStep;
    private int alternator;
    private Vertex previouslyPlacedVertex;
    private Set<Vertex> verticesPartitionA;
    private Set<Vertex> verticesPartitionB;
    private boolean widthIsShorter;
    private int roundCounter;
    private Coordinate cornerWithLeastDensity;
    private Role role;

    @Override
    public GameMove maximizeCrossings(GameMove lastMove) {
        return null;
    }

    @Override
    public GameMove minimizeCrossings(GameMove lastMove) {
        return null;
    }

    @Override
    public GameMove maximizeCrossingAngles(GameMove lastMove) {
        applyLastMove(lastMove, gs);
        GameMove move;
        try {
            move = getMaximizerAngleMove();
        } catch (Exception e) {
            move = randomMove(g, gs, r, width, height);
        }
        gs.applyMove(move);
        return move;
    }

    private GameMove getMaximizerAngleMove() {
        Vertex v = getVertexToPlace();
        previouslyPlacedVertex = v;
        //save vertex to be placed into corresponding partition
        if (alternator % 2 == 0) {
            verticesPartitionA.add(v);
        } else {
            verticesPartitionB.add(v);
        }
        Coordinate coordinate = getCoordinate();
        GameMove move = new GameMove(v, coordinate);

        //adjust step and alternator values for next round
        alternator++;
        int modulus = (widthIsShorter ? width : height) / 4;
        if (alternator % 2 == 0) innerLoopStep++;
        outerLoopStep += innerLoopStep / modulus;
        innerLoopStep %= modulus;

        return move;
    }

    private Vertex getVertexToPlace() {
        //get a free neighbor of the previously placed vertex
        //or any free vertex in the first round
        Vertex v;
        if (previouslyPlacedVertex == null) {
            v = getAnyFreeVertex(g, gs);
        } else {
            try {
                v = getFreeNeighbors(previouslyPlacedVertex, g, gs).iterator().next();
            } catch (NoSuchElementException e) {
                v = null;
            }
        }

        //get a free neighbor of the opposing partition if the previously placed vertex has no free neighbors
        if (role == Role.MAX_ANGLE && v == null) {
            v = getFreeNeighborOfOpposingPartition();
        }

        //fallback: get any free vertex
        if (v == null) {
            v = getAnyFreeVertex(g, gs);
        }
        return v;
    }

    private Vertex getFreeNeighborOfOpposingPartition() {
        return getAnyFreeNeighborOfVertexSet(alternator % 2 == 0 ? verticesPartitionB : verticesPartitionA, g, gs);
    }

    private Coordinate getCoordinate() {
        int x, y;
        if (widthIsShorter) {
            x = (alternator % 2) == 0 ? innerLoopStep : width - 1 - innerLoopStep;
            y = (alternator % 2) == 0 ? outerLoopStep : height - 1 - outerLoopStep;
        } else {
            x = (alternator % 2) == 0 ? outerLoopStep : width - 1 - outerLoopStep;
            y = (alternator % 2) == 0 ? innerLoopStep : height - 1 - innerLoopStep;
        }
        Coordinate coordinate = new Coordinate(x, y);
        return findClosestUnusedCoordinate(gs, coordinate, width, height);
    }

    @Override
    public GameMove minimizeCrossingAngles(GameMove lastMove) {
        applyLastMove(lastMove, gs);
        GameMove move;
        try {
            move = getMinimizerAngleMove();
        } catch (Exception e) {
            move = randomMove(g, gs, r, width, height);
        }
        gs.applyMove(move);
        return move;
    }

    private GameMove getMinimizerAngleMove() {
        if (roundCounter < 10) {
            roundCounter++;
            return getDefaultMinimizerMove(g, gs, r, width, height);
        } else {
            return getMinimizerCornerMove();
        }
    }

    private GameMove getMinimizerCornerMove() {
        if (cornerWithLeastDensity == null) {
            identifyCornerWithLeastDensity();
        }
        Vertex v = getVertexToPlace();
        previouslyPlacedVertex = v;
        return new GameMove(v, findClosestUnusedCoordinate(gs, cornerWithLeastDensity, width, height));
    }

    /**
     * Identifies corner with the least density.
     * 0 -> topLeft, 1 -> topRight, 2 -> bottomLeft, 3 -> bottomRight
     */
    private void identifyCornerWithLeastDensity() {
        Map<Integer, Integer> densities = new HashMap<>(Map.of(
                0, 0,
                1, 0,
                2, 0,
                3, 0
        ));

        //fill out density list
        for (Coordinate coordinate : gs.getVertexCoordinates().values()) {
            if (coordinate.getX() < (width / 2) && coordinate.getY() < (height / 2)) {
                //top left
                densities.put(0, densities.get(0) + 1);
            } else if (coordinate.getX() > (width / 2) && coordinate.getY() < (height / 2)) {
                //top right
                densities.put(1, densities.get(1) + 1);
            } else if (coordinate.getX() < (width / 2) && coordinate.getY() > (height / 2)) {
                //bottom left
                densities.put(2, densities.get(2) + 1);
            } else {
                //bottom right
                densities.put(3, densities.get(3) + 1);
            }
        }
        switch (Collections.min(densities.entrySet(), Map.Entry.comparingByValue()).getKey()) {
            case 0:
                cornerWithLeastDensity = new Coordinate(0, 0);
                break;
            case 1:
                cornerWithLeastDensity = new Coordinate(width, 0);
                break;
            case 2:
                cornerWithLeastDensity = new Coordinate(0, height);
                break;
            case 3:
                cornerWithLeastDensity = new Coordinate(width, height);
                break;
        }
    }

    @Override
    public void initializeNextRound(Graph g, int width, int height, Role role) {
        this.g = g;
        this.width = width;
        this.height = height;
        this.gs = new GameState(g, width, height);
        this.r = new Random();
        this.innerLoopStep = 0;
        this.outerLoopStep = 0;
        this.widthIsShorter = width < height;
        this.verticesPartitionA = new HashSet<>();
        this.verticesPartitionB = new HashSet<>();
        this.alternator = 0;
        this.previouslyPlacedVertex = null;
        this.roundCounter = 0;
        this.cornerWithLeastDensity = null;
        this.role = role;
    }

    @Override
    public String getName() {
        return "Gruppe 10";
    }
}
