package GraphXings.Gruppe5Algo.Players;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Data.*;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameState;
import GraphXings.Gruppe5Algo.Algorithms.BasicCrossingCalculatorAlgorithm;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RandomChoicePlayer implements NewPlayer {

    private final int maxPoints;

    private final int timoutMilliseconds;
    /**
     * The name of the random player.
     */
    private String name;
    /**
     * The graph to be drawn.
     */
    private Graph g;
    /**
     * The current state of the game;
     */
    private GameState gs;
    /**
     * The width of the game board.
     */
    private int width;
    /**
     * The height of the game board.
     */
    private int height;

    /**
     * Creates a random player with the assigned name.
     *
     * @param name : name of the player competing
     */
    public RandomChoicePlayer(String name, int maxPointsPerMove, int timoutMilliseconds) {
        this.name = name;
        this.maxPoints = maxPointsPerMove;
        this.timoutMilliseconds = timoutMilliseconds;
    }

    private GameMove getRandomGameMove() {
        Random r = new Random();
        int stillToBePlaced = g.getN() - gs.getPlacedVertices().size();
        int next = r.nextInt(stillToBePlaced);
        int skipped = 0;
        Vertex v = null;
        for (Vertex u : g.getVertices()) {
            if (!gs.getPlacedVertices().contains(u)) {
                if (skipped < next) {
                    skipped++;
                    continue;
                }
                v = u;
                break;
            }
        }
        Coordinate c = new Coordinate(0, 0);
        do {
            c = new Coordinate(r.nextInt(width), r.nextInt(height));
        } while (gs.getUsedCoordinates()[c.getX()][c.getY()] != 0);
        return new GameMove(v, c);
    }

    public GameMove maximizeCrossings(GameMove lastMove) {

        if (lastMove != null) {
            gs.applyMove(lastMove);
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Your task to be executed
        Callable<GameMove> task = () -> {
            GameMove betterMove = betterMove(true);
            gs.applyMove(betterMove);
            return betterMove;
        };

        // Submit the task to the executor
        Future<GameMove> future = executorService.submit(task);
        GameMove move = null;
        try {
            move = future.get(timoutMilliseconds, TimeUnit.MILLISECONDS); // 2 seconds timeout
        } catch (TimeoutException e) {
            move = getRandomGameMove();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // Handle other exceptions
        } finally {
            executorService.shutdown();
        }
        return move;
    }

    public GameMove minimizeCrossings(GameMove lastMove) {

        if (lastMove != null) {
            gs.applyMove(lastMove);
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Callable<GameMove> task = () -> {
            GameMove betterMove = betterMove(false);
            gs.applyMove(betterMove);
            return betterMove;
        };

        // Submit the task to the executor
        Future<GameMove> future = executorService.submit(task);
        GameMove move = null;
        try {
            // Set a timeout for the task
            move = future.get(timoutMilliseconds, TimeUnit.MILLISECONDS); // 2 seconds timeout
        } catch (TimeoutException e) {
            System.out.println("TIMOUT, getting random move");
            move = getRandomGameMove();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // Handle other exceptions
        } finally {
            // Shutdown the executor
            executorService.shutdown();
        }
        // gs.applyMove(move);
        return move;

    }

    @Override
    public void initializeNextRound(Graph g, int width, int height, Role role) {
        this.g = g;
        this.width = width;
        this.height = height;
        this.gs = new GameState(width, height);
    }

    private Coordinate getRandomUnusedCoord(Random r) {

        var x = r.nextInt(width);
        var y = r.nextInt(height);
        do {
            x = r.nextInt(width);
            y = r.nextInt(height);
        } while (gs.getUsedCoordinates()[x][y] != 0);

        return new Coordinate(x, y);

    }

    private Boolean isCoordiateValid(Coordinate c) {
        int x = c.getX();
        int y = c.getY();

        if (x >= width || y >= height || gs.getUsedCoordinates()[x][y] != 0) {
            return false;
        }

        return true;
    }

    private Coordinate getNotRandomUnusedCoord(Random r, boolean findMax) {
        double maxFieldSizeFactor = 0.9;
        double minFieldSizeFactor = 0.4;
        Coordinate c;

        do {
            if (findMax) {
                double maxFieldLength = 1 - maxFieldSizeFactor;
                int sizeOfField = (int) ((maxFieldLength * width) * (maxFieldLength * height));

                if (sizeOfField < g.getN()) {
                    // c = getAlternativeInterval();
                    c = getRandomUnusedCoord(r);
                } else {
                    c = new Coordinate(r.nextInt((int) (maxFieldSizeFactor * width), width),
                            r.nextInt((int) (maxFieldSizeFactor * height), height));
                }
            } else {
                double minFieldLength = Math.abs(minFieldSizeFactor - (1 - minFieldSizeFactor));
                int sizeOfField = (int) ((minFieldLength * width) * (minFieldLength * height));

                if (sizeOfField < g.getN()) {
                    // c = getAlternativeInterval(height, g.getN());
                    c = getRandomUnusedCoord(r);
                } else {
                    c = new Coordinate(
                            r.nextInt((int) (minFieldSizeFactor * width), (int) ((1 - minFieldSizeFactor) * width)),
                            r.nextInt((int) (minFieldSizeFactor * height), (int) ((1 - minFieldSizeFactor) * height)));
                }
            }
        } while ((gs.getUsedCoordinates()[c.getX()][c.getY()] != 0) && !isCoordiateValid(c));

        return c;
    }

    private Vertex chooseNextVertex() {
        Vertex alternativeVertexNoEdge = new Vertex("new");

        for (Vertex v : g.getVertices()) {
            if (!gs.getPlacedVertices().contains(v)) {
                Stream<Edge> Edges = StreamSupport.stream(g.getIncidentEdges(v).spliterator(), false);
                if (Edges.count() >= 1) {
                    return v;
                } else {
                    alternativeVertexNoEdge = v;
                }
            }
        }
        return alternativeVertexNoEdge;
    }

    private int[][] copyUsedCoordinates() {
        int[][] copy = new int[gs.getUsedCoordinates().length][gs.getUsedCoordinates()[0].length];
        for (int i = 0; i < gs.getUsedCoordinates().length; i++) {
            for (int j = 0; j < gs.getUsedCoordinates()[0].length; j++) {
                copy[i][j] = gs.getUsedCoordinates()[i][j];
            }
        }
        return copy;
    }

    private HashMap<Vertex, Coordinate> copyVertexCoordinates() {
        HashMap<Vertex, Coordinate> copy = new HashMap<>();
        for (Vertex v : gs.getVertexCoordinates().keySet()) {
            copy.put(v, gs.getVertexCoordinates().get(v));
        }
        return copy;
    }

    /**
     * Computes a random valid move.
     *
     * @param findMax: flag if the maximization or the minimization is needed
     * @return A random valid move.
     */
    private GameMove betterMove(boolean findMax) {
        Random r = new Random();

        // Vertex v = null;
        Vertex v = chooseNextVertex();

        int maxAvailableCoords = (width * height) - gs.getPlacedVertices().size();
        var newUsedCoords = copyUsedCoordinates();
        var randomCoords = new ArrayList<Coordinate>();
        int numPoints = Math.min(maxAvailableCoords, maxPoints);

        for (var i = 0; i < numPoints; i++) {
            var newCoord = getNotRandomUnusedCoord(r, findMax);
            newUsedCoords[newCoord.getX()][newCoord.getY()] = 1;
            randomCoords.add(newCoord);
        }
        if (findMax) {
            var c = getBestOfPlacement(randomCoords, v);
            return new GameMove(v, c);
        } else {
            var c = getWorstOfPlacement(randomCoords, v);
            return new GameMove(v, c);

        }

    }

    private int calculateNewEdgeCrossings(Vertex newPlacedVertex) {
        int crossingNumber = 0;
        var adjacentEdges = g.getIncidentEdges(newPlacedVertex);

        var vertexCoordinates = this.gs.getVertexCoordinates();
        for (Edge e1 : adjacentEdges) {

            for (Edge e2 : g.getEdges()) {
                if (!e1.equals(e2)) {
                    if (!e1.isAdjacent(e2)) {

                        if (!vertexCoordinates.containsKey(e1.getS()) || !vertexCoordinates.containsKey(e1.getT())
                                || !vertexCoordinates.containsKey(e2.getS())
                                || !vertexCoordinates.containsKey(e2.getT())) {
                            continue;
                        }
                        Segment s1 = new Segment(vertexCoordinates.get(e1.getS()), vertexCoordinates.get(e1.getT()));
                        Segment s2 = new Segment(vertexCoordinates.get(e2.getS()), vertexCoordinates.get(e2.getT()));
                        if (Segment.intersect(s1, s2)) {
                            crossingNumber++;
                        }
                    }
                }
            }
        }
        return crossingNumber / 2;
    }

    private Coordinate getBestOfPlacement(ArrayList<Coordinate> possibleCoordinates, Vertex vertexToBePlaced) {

        Coordinate coordinateWithMaxCross = possibleCoordinates.get(0);
        int max = 0;
        var newVertexCoordinates = copyVertexCoordinates();

        for (Coordinate c : possibleCoordinates) {
            // place vertex
            newVertexCoordinates.put(vertexToBePlaced, c);
            int currentCrossingNum = calculateNewEdgeCrossings(vertexToBePlaced);

            if (currentCrossingNum >= max) {
                max = currentCrossingNum;
                coordinateWithMaxCross = c;
            }
        }
        return coordinateWithMaxCross;
    }

    private Coordinate getWorstOfPlacement(ArrayList<Coordinate> possibleCoordinates,
            Vertex vertexToBePlaced) {

        Coordinate coordinateWithMinCross = possibleCoordinates.get(0);
        int min = 1000000000;
        var newVertexCoordinates = copyVertexCoordinates();

        for (Coordinate c : possibleCoordinates) {
            // place vertex
            newVertexCoordinates.put(vertexToBePlaced, c);

            int currentCrossingNum = calculateNewEdgeCrossings(vertexToBePlaced);
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