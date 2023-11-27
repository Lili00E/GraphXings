package stuffUnused;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Data.*;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameState;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;

public class RandomChoicePlayerTest implements NewPlayer {

    private final int maxPoints;
    private final int timoutMilliseconds;

    private String name;

    private Graph g;
    private GameState gs;

    private int width;
    private int height;

    public RandomChoicePlayerTest(String name, int maxPointsPerMove, int timoutMilliseconds) {
        this.name = name;
        this.maxPoints = maxPointsPerMove;
        this.timoutMilliseconds = timoutMilliseconds;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void initializeNextRound(Graph g, int width, int height, Role role) {
        this.g = g;
        this.width = width;
        this.height = height;
        this.gs = new GameState(width, height);
    }

    @Override
    public GameMove maximizeCrossings(GameMove lastMove) {
        return findMoveWithTimeout(lastMove, true);

    }

    @Override
    public GameMove minimizeCrossings(GameMove lastMove) {

        return findMoveWithTimeout(lastMove, false);

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

    private ArrayList<Coordinate> getNotRandomUnusedCoord() {
        Random r = new Random();
        int intervalSizeX = width / 10;
        int intervalSizeY = height / 10;
        ArrayList<Coordinate> coords = new ArrayList<>();
        int x;
        int y;

        for (int i = intervalSizeX; i < width; i += intervalSizeX) {
            for (int j = intervalSizeY; j < height; j += intervalSizeY) {
                do {
                    x = r.nextInt(i - (i - intervalSizeX)) + (i - intervalSizeX);
                    y = r.nextInt(j - (j - intervalSizeY)) + (j - intervalSizeY);
                } while ((x >= width) || (y >= height) || (gs.getUsedCoordinates()[x][y] != 0));
                coords.add(new Coordinate(x, y));
            }
        }
        return coords;
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

    public GameMove findMoveWithTimeout(GameMove lastMove, boolean maximizeCrossings) {

        if (lastMove != null) {
            gs.applyMove(lastMove);
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Your task to be executed
        Callable<GameMove> task = () -> {
            GameMove betterMove = findMove(maximizeCrossings);
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

    private Vertex getNextVertex() {
        Vertex alternativeVertex = null;

        for (Vertex v : g.getVertices()) {
            if (!gs.getPlacedVertices().contains(v)) {
                var edges = g.getIncidentEdges(v);
                if (edges.iterator().hasNext()) {
                    return v;
                } else {
                    alternativeVertex = v;
                }
            }
        }
        return alternativeVertex;
    }

    private ArrayList<Coordinate> getRandomUnusedCoordinates(int numCoordinates) {
        Random r = new Random();
        var randomCoords = new ArrayList<Coordinate>();

        for (var i = 0; i < numCoordinates; i++) {
            var newCoord = getRandomUnusedCoord(r);
            randomCoords.add(newCoord);
        }
        return randomCoords;
    }

    private GameMove findMove(boolean maximizeCrossings) {

        int maxAvailableCoords = (width * height) - gs.getPlacedVertices().size();
        int numPoints = Math.min(maxAvailableCoords, maxPoints);
        var randomCoords = getRandomUnusedCoordinates(numPoints);
        Random r = new Random();
        ArrayList<Coordinate> possibleCoords = getNotRandomUnusedCoord();
        Vertex v = getNextVertex();

        if (maximizeCrossings) {
            Coordinate c = getBestOfPlacement(possibleCoords, v);
            return new GameMove(v, c);
        } else {
            Coordinate c = getWorstOfPlacement(possibleCoords, v);
            return new GameMove(v, c);
        }

    }

    private Coordinate getBestOfPlacement(ArrayList<Coordinate> possibleCoordinates, Vertex vertexToBePlaced) {

        Coordinate coordinateWithMaxCross = possibleCoordinates.get(0);
        int max = 0;

        for (Coordinate c : possibleCoordinates) {

            var move = new GameMove(vertexToBePlaced, c);

            int currentCrossingNum = calculateNewEdgeCrossings(move);

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

        for (Coordinate c : possibleCoordinates) {

            var move = new GameMove(vertexToBePlaced, c);

            int currentCrossingNum = calculateNewEdgeCrossings(move);
            if (currentCrossingNum < min) {
                min = currentCrossingNum;
                coordinateWithMinCross = c;
            }
        }

        return coordinateWithMinCross;
    }

    private void undoMove(GameMove move) {

        var usedCoordinates = gs.getUsedCoordinates();
        var placedVertices = gs.getPlacedVertices();
        var vertexCoordinates = gs.getVertexCoordinates();
        usedCoordinates[move.getCoordinate().getX()][move.getCoordinate().getY()] = 0;
        placedVertices.remove(move.getVertex());
        vertexCoordinates.remove(move.getVertex());

    }

    private void applyMove(GameMove move) {
        gs.applyMove(move);
    }

    private int calculateNewEdgeCrossings(GameMove newMove) {

        applyMove(newMove);
        int crossingNumber = 0;
        var adjacentEdges = g.getIncidentEdges(newMove.getVertex());
        var vertexCoordinates = this.gs.getVertexCoordinates();

        // TODO: too many edges predicted...
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
        undoMove(newMove);
        return crossingNumber;
    }

}