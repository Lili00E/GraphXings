package GraphXings.Gruppe8;

import GraphXings.Algorithms.CrossingCalculator;
import GraphXings.Algorithms.NewPlayer;
import GraphXings.Data.Coordinate;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Segment;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameState;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * A player performing random moves.
 */
public class EfficientWinningPlayer implements NewPlayer {
    /**
     * The name of the random player.
     */
    private String name;
    /**
     * A random number generator.
     */
    private Random r;
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
     * @param name
     */
    public EfficientWinningPlayer(String name) {
        this.name = name;
        this.r = new Random(name.hashCode());
    }

    @Override
	public GameMove maximizeCrossingAngles(GameMove lastMove)
	{
		// First: Apply the last move by the opponent.
		if (lastMove != null) {
            gs.applyMove(lastMove);
        }
        GameMove move = max(lastMove);
        gs.applyMove(move);
        return move;
	}

	@Override
	public GameMove minimizeCrossingAngles(GameMove lastMove)
	{
		// First: Apply the last move by the opponent.
		if (lastMove != null) {
            gs.applyMove(lastMove);
        }

        GameMove move = min(lastMove);
        gs.applyMove(move);
        return move;
	}

    @Override
    public GameMove maximizeCrossings(GameMove lastMove) {
        if (lastMove != null) {
            gs.applyMove(lastMove);
        }
        GameMove move = max(lastMove);
        gs.applyMove(move);
        return move;
    }

    @Override
    public GameMove minimizeCrossings(GameMove lastMove) {
        if (lastMove != null) {
            gs.applyMove(lastMove);
        }

        GameMove move = min(lastMove);
        gs.applyMove(move);
        return move;
    }

    @Override
    public void initializeNextRound(Graph g, int width, int height, Role role) {
        this.g = g;
        this.width = width;
        this.height = height;
        this.gs = new GameState(g, width, height);
        // System.out.println(role.toString());
    }

    /**
     * Computes a random valid move.
     * 
     * @param g               The graph.
     * @param usedCoordinates The used coordinates.
     * @param placedVertices  The already placed vertices.
     * @param width           The width of the game board.
     * @param height          The height of the game board.
     * @return A random valid move.
     */
    private GameMove randomMove() {
        // System.err.println("rand");
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
        Coordinate c;
        do {
            c = new Coordinate(r.nextInt(width), r.nextInt(height));
        } while (gs.getUsedCoordinates()[c.getX()][c.getY()] != 0);
        return new GameMove(v, c);
    }

    // private GameMove powerRandomMove(int xCoord, int yCoord) {
    //     // System.err.println("rand");
    //     int stillToBePlaced = g.getN() - gs.getPlacedVertices().size();
    //     int next = r.nextInt(stillToBePlaced);
    //     int skipped = 0;
    //     Vertex v = null;
    //     for (Vertex u : g.getVertices()) {
    //         if (!gs.getPlacedVertices().contains(u)) {
    //             if (skipped < next) {
    //                 skipped++;
    //                 continue;
    //             }
    //             v = u;
    //             break;
    //         }
    //     }
    //     Coordinate c;
    //     do {
    //         xCoord = xCoord + 1;
    //         yCoord = yCoord + 1;
    //         c = new Coordinate(xCoord, yCoord);
    //     } while (gs.getUsedCoordinates()[c.getX()][c.getY()] != 0);
    //     return new GameMove(v, c);
    // }

    public boolean hasOnlyFreeNeighbours(HashSet<Vertex> neighbors) {
        for (Vertex v : neighbors) {
            if (!gs.getPlacedVertices().contains(v)) {
                return false;
            }
        }
        return true;
    }

    private GameMove min(GameMove lastMove) {
        // System.out.println(gs.getPlacedVertices().size());
        if (getSizeOfIterableVertex(g.getVertices()) <= 84) {
            // System.out.println("brut");
            return minBrutGameMove(1000);
        }

        if (lastMove != null) {
            Vertex lastVertex = lastMove.getVertex();
                Vertex notPlacVertex = null;
                // HashMap<Vertex, HashSet<Vertex>> newVertexList = new HashMap<Vertex, HashSet<Vertex>>();
                for (Vertex v : gs.getPlacedVertices()) {
                    Set<Vertex> neigh = getNeighbors(v);
                    for (Vertex vv : neigh) {
                        if (!gs.getPlacedVertices().contains(vv)) {
                            lastVertex = v;
                            notPlacVertex = vv;
                            break;
                        }
                    }
                }

                if (notPlacVertex == null) {
                    for (Vertex v : g.getVertices()) {
                        if (!gs.getPlacedVertices().contains(v)) {
                            notPlacVertex = v;
                            break;
                        }
                    }
                }
                Coordinate c = getCoordinateOfVertex(lastVertex);
                // System.out.println(c.getX() + " " + c.getY());
                getPositions pos = new getPositions();
                Coordinate coords = pos.getNextFreeCoordinate(c, gs.getUsedCoordinates(), width, height);
                // System.out.println(coords.size() + "sizzze");
                GameMove newMove = new GameMove(notPlacVertex, coords);

                if (gs.checkMoveValidity(newMove)) {
                    // System.out.println("min first");
                    return newMove;
                } else {
                    // System.out.println("min rand");
                    return randomMove();
                }
        }

        // System.out.println("not here min");
        return randomMove();

    }

    private GameMove max(GameMove lastMove) {

        if (getSizeOfIterableVertex(g.getVertices()) <= 84) {
            // System.out.println("brut");
            return maxBrutGameMove(1000);
        }

        if (lastMove != null) {
            try {
                Vertex lastVertex = lastMove.getVertex();
                Vertex notPlacVertex = null;

                for (Vertex v : gs.getPlacedVertices()) {
                    Set<Vertex> neigh = getNeighbors(v);

                    for (Vertex vv : neigh) {
                        if (!gs.getPlacedVertices().contains(vv)) {
                            lastVertex = v;
                            notPlacVertex = vv;

                            break;
                        }
                    }
                }

                if (notPlacVertex == null) {
                    for (Vertex v : g.getVertices()) {
                        if (!gs.getPlacedVertices().contains(v)) {
                            notPlacVertex = v;
                            break;
                        }
                    }
                }
                Coordinate c = getCoordinateOfVertex(lastVertex);
                // DensityFinder densityRegionsFinder = new DensityFinder();
                // Coordinate newCoordinate = densityRegionsFinder.findCoordinateInMaxDensityRegion(gs.getUsedCoordinates());

                Coordinate mirrorCoordinate = farthestPoint(c);
                GameMove newMove = new GameMove(notPlacVertex, mirrorCoordinate);

                // Random random = new Random();
                // int randInt = random.nextInt(10);
                // if (randInt % 5 == 0) {
                //     Set<GameMove> possibleGameMoves = createPossibleMoves(lastVertex);
                //     newMove = testMaxBrut(possibleGameMoves);
                // }

                if (gs.checkMoveValidity(newMove)) {
                    // System.out.println("max first");
                    return newMove;
                } else {
                    getPositions positions = new getPositions();
                    Coordinate coords = positions.getNextFreeCoordinate(mirrorCoordinate, gs.getUsedCoordinates(), width, height);
                    newMove = new GameMove(notPlacVertex, coords);

                    if (gs.checkMoveValidity(newMove)) {
                        // System.out.println("max second");
                        return newMove;
                    } else {
                        // System.out.println("max rand");
                        return randomMove();
                    }
                }
            } catch (Exception e) {
                // System.out.println(e + " max");
                return randomMove();
            }
        }

        return randomMove();

    }

    private Coordinate getPossibleCoordinate(Coordinate inputCoordinate) {
        Random random = new Random();
        int rand = random.nextInt(4);

        if (rand == 0) {
            return topLeftPoint(inputCoordinate);
        } else if (rand == 1) {
            return topRightPoint(inputCoordinate);
        } else if (rand == 2) {
            return bottomLeftPoint(inputCoordinate);
        } else if (rand == 3) {
            return bottomRightPoint(inputCoordinate);
        } else {
            return mirrorPoint(inputCoordinate);
        }
    }

    public Coordinate topLeftPoint(Coordinate c) {
        int fieldHeight = height;
        int fieldWidth = width;
        int mirroredX = Math.min(c.getX(), fieldWidth / 2);
        int mirroredY = Math.min(c.getY(), fieldHeight / 2);
        return new Coordinate(mirroredX, mirroredY);
    }
    
    public Coordinate topRightPoint(Coordinate c) {
        int fieldHeight = height;
        int fieldWidth = width;
        int mirroredX = Math.max(c.getX(), fieldWidth / 2);
        int mirroredY = Math.min(c.getY(), fieldHeight / 2);
        return new Coordinate(mirroredX, mirroredY);
    }
    
    public Coordinate bottomLeftPoint(Coordinate c) {
        int fieldHeight = height;
        int fieldWidth = width;
        int mirroredX = Math.min(c.getX(), fieldWidth / 2);
        int mirroredY = Math.max(c.getY(), fieldHeight / 2);
        return new Coordinate(mirroredX, mirroredY);
    }
    
    public Coordinate bottomRightPoint(Coordinate c) {
        int fieldHeight = height;
        int fieldWidth = width;
        int mirroredX = Math.max(c.getX(), fieldWidth / 2);
        int mirroredY = Math.max(c.getY(), fieldHeight / 2);
        return new Coordinate(mirroredX, mirroredY);
    }
    

    private Set<GameMove> createPossibleMoves(Vertex lastVertex) {
        Coordinate lastVertexCoordinate = getCoordinateOfVertex(lastVertex);
        Set<GameMove> possibleGameMoves = new HashSet<>();
        Vertex bestVertex = getVertexWithMostIndicentEdges(lastVertex);

        Coordinate mirrorCoordinate = mirrorPoint(lastVertexCoordinate);
        GameMove mirror = new GameMove(bestVertex, mirrorCoordinate);

        Coordinate mirrorLenCoordinate = mirrorPointMax(lastVertexCoordinate);
        GameMove mirrorLen = new GameMove(bestVertex, mirrorLenCoordinate);

        possibleGameMoves.add(mirror);
        possibleGameMoves.add(mirrorLen);
        possibleGameMoves.add(randomMove());
        possibleGameMoves.add(randomMove());
        possibleGameMoves.add(randomMove());
        possibleGameMoves.add(randomMove());
        possibleGameMoves.add(randomMove());
        
        return possibleGameMoves;

    }

    public Coordinate mirrorPoint(Coordinate c) {
        int fieldHeight = height;
        int fieldWidth = width;
    
        int mirroredX = (fieldWidth - 1) - c.getX();
        int mirroredY = (fieldHeight - 1) - c.getY();
        return new Coordinate(mirroredX, mirroredY);
    }

    public Coordinate farthestPoint(Coordinate c) {
        int fieldHeight = height;
        int fieldWidth = width;
    
        int farthestX, farthestY;
    
        if (c.getX() <= fieldWidth / 2) {
            farthestX = fieldWidth - 1;
        } else {
            farthestX = 0;
        }
    
        if (c.getY() <= fieldHeight / 2) {
            farthestY = fieldHeight - 1;
        } else {
            farthestY = 0;
        }
    
        return new Coordinate(farthestX, farthestY);
    }
    

    public Coordinate mirrorPointMax(Coordinate c) {
        int fieldHeight = height; 
        int fieldWidth = width; 

        int farthestX, farthestY;

        if (c.getX() < fieldWidth / 2 && c.getY() < fieldHeight / 2) {
            farthestX = fieldWidth - 1;
            farthestY = fieldHeight - 1;
        } else if (c.getX() >= fieldWidth / 2 && c.getY() < fieldHeight / 2) {
            farthestX = 0;
            farthestY = fieldHeight - 1;
        } else if (c.getX() < fieldWidth / 2 && c.getY() >= fieldHeight / 2) {
            farthestX = fieldWidth - 1;
            farthestY = 0;
        } else {
            farthestX = 0;
            farthestY = 0;
        }

        return new Coordinate(farthestX, farthestY);
    }
    

    private Vertex getVertexWithMostIndicentEdges(Vertex lastVertex) {
        Vertex besVertex = null;
        int amountEdges = 0;
        for (Vertex v : g.getVertices()) {
            Iterable<Edge> edges = g.getIncidentEdges(v);
            int estimatedSize = getSizeOfIterable(edges);
            if (estimatedSize > amountEdges && !gs.getPlacedVertices().contains(v)) {
                besVertex = v;
                amountEdges = estimatedSize;
                // System.out.println(amountEdges);
            }
        }

        return besVertex;
    }

    private Coordinate getCoordinateOfVertex(Vertex v) {
        for (Map.Entry<Vertex, Coordinate> entry : gs.getVertexCoordinates().entrySet()) {
            Vertex vertex = entry.getKey();
            Coordinate coordinate = entry.getValue();
            if (vertex.getId() == v.getId()) {
                return coordinate;
            }
        }

        return new Coordinate(0, 0);
    }

    public Set<Vertex> getNeighbors(Vertex v) {
        Iterable<Edge> edges = g.getIncidentEdges(v);

        int estimatedSize = (edges instanceof Collection) ? ((Collection) edges).size() : 16;

        return StreamSupport.stream(edges.spliterator(), false) 
                            .map(e -> e.getS().equals(v) ? e.getT() : e.getS())
                            .collect(Collectors.toCollection(() -> new HashSet<>(estimatedSize)));
    }

    public int getSizeOfIterable(Iterable<Edge> edges) {
        int count = 0;
        for (Edge edge : edges) {
            count++;
        }
        return count;
    }

    public boolean hastLastVertex(Iterable<Edge> edges, Vertex lastVertex) {
        for (Edge edge : edges) {
            if (edge.getS() == lastVertex || edge.getT() == lastVertex) {
                return true;
            }
        }
        return false;
    }

    public int getSizeOfIterableVertex(Iterable<Vertex> vertices) {
        int count = 0;
        for (Vertex v : vertices) {
            count++;
        }
        return count;
    }
    // private GameMove maxBrutGameMove() {
    //     int iterations = width * height;

    //     if (iterations > 4000000) {
    //         iterations = 4000000;
    //     }

    //     int max = Integer.MIN_VALUE;
    //     GameMove randGameMove = new GameMove(null, null);
    //     GameMove bestMove = randomMove();
    //     for (int i = 0; i < iterations; i++) {
    //         randGameMove = randomMove();
    //         HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>(gs.getVertexCoordinates());
    //         vertexCoordinates.put(randGameMove.getVertex(), randGameMove.getCoordinate());
    //         CrossingCalculator crossingCalculator = new CrossingCalculator(g, vertexCoordinates);
    //         int number = crossingCalculator.computeCrossingNumber();

    //         if (number > max) {
    //             max = number;
    //             bestMove = randGameMove;
    //         }

    //     }

    //     return bestMove;
    // }

    private GameMove testMaxBrut(Set<GameMove> possibleMoves) {
        AtomicInteger max = new AtomicInteger(Integer.MIN_VALUE);
        AtomicReference<GameMove> bestMove = new AtomicReference<>(null);
    
        possibleMoves.parallelStream().forEach(move -> {
            HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>(gs.getVertexCoordinates());
            vertexCoordinates.put(move.getVertex(), move.getCoordinate());

            // SweepLineIntersection sweepLineIntersection = new SweepLineIntersection();
            // int number = sweepLineIntersection.computeCrossingNumber(g, vertexCoordinates);

            CrossingCalculator crossingCalculator = new CrossingCalculator(g, vertexCoordinates);
            int number = crossingCalculator.computeCrossingNumber();
    
            synchronized (this) {
                if (number > max.get()) {
                    // System.out.println(number);
                    max.set(number);
                    bestMove.set(move);
                }
            }
        });
    
        return bestMove.get();
    }
    

    private GameMove maxBrutGameMove(int amountIterations) {
        int field = width * height / 2;
        int iterations = Math.min(field, amountIterations);
        AtomicInteger max = new AtomicInteger(Integer.MIN_VALUE);
        AtomicReference<GameMove> bestMove = new AtomicReference<>(randomMove());

        IntStream.range(0, iterations).parallel().forEach(i -> {
            GameMove randGameMove = randomMove();
            HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>(gs.getVertexCoordinates());
            vertexCoordinates.put(randGameMove.getVertex(), randGameMove.getCoordinate());
            CrossingCalculator crossingCalculator = new CrossingCalculator(g, vertexCoordinates);
            int number = crossingCalculator.computeCrossingNumber();

            // SweepLineIntersection sweepLineIntersection = new SweepLineIntersection();
            // int number = sweepLineIntersection.computeCrossingNumber(g, vertexCoordinates);
            // EfficientCrossingCalculator efficientCrossingCalculator = new EfficientCrossingCalculator(g, vertexCoordinates);
            // int number = efficientCrossingCalculator.computeCrossingNumber();

            synchronized (bestMove) {
                if (number > max.get()) {
                    max.set(number);
                    bestMove.set(randGameMove);
                }
            }
        });

        return bestMove.get();
    }

    private GameMove minBrutGameMove(int amountIterations) {
        int field = width * height / 2;
        int iterations = Math.min(field, amountIterations);
        AtomicInteger min = new AtomicInteger(Integer.MAX_VALUE);
        AtomicReference<GameMove> bestMove = new AtomicReference<>(randomMove());

        IntStream.range(0, iterations).parallel().forEach(i -> {
            GameMove randGameMove = randomMove();
            HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>(gs.getVertexCoordinates());
            vertexCoordinates.put(randGameMove.getVertex(), randGameMove.getCoordinate());
            CrossingCalculator crossingCalculator = new CrossingCalculator(g, vertexCoordinates);
            int number = crossingCalculator.computeCrossingNumber();

            // SweepLineIntersection sweepLineIntersection = new SweepLineIntersection();
            // int number = sweepLineIntersection.computeCrossingNumber(g, vertexCoordinates);

            // EfficientCrossingCalculator efficientCrossingCalculator = new EfficientCrossingCalculator(g, vertexCoordinates);
            // int number = efficientCrossingCalculator.computeCrossingNumber();

            synchronized (bestMove) {
                if (number < min.get()) {
                    min.set(number);
                    bestMove.set(randGameMove);
                }
            }
        });

        return bestMove.get();
    }

public class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}


    @Override
    public String getName() {
        return name;
    }

}