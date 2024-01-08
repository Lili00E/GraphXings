
package GraphXings.Gruppe5.Players;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Data.*;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameState;
import GraphXings.Gruppe5.Models.ScoredCoordinate;

import java.util.*;
import java.util.concurrent.*;

public class RecursiveSearchPlayer implements NewPlayer {

  private final int timoutMilliseconds;

  private String name;

  private Graph g;
  private GameState gs;

  private int width;
  private int height;

  private Role role;

  private int numberOfPositionsPerCell;
  private int searchDepth;
  private int gridDivider;

  private GameMove bestMove;

  public RecursiveSearchPlayer(
      String name,
      int searchDepth,
      int gridSize,
      int numberOfPositionsPerCell,
      int timoutMilliseconds) {
    this.name = name;
    this.timoutMilliseconds = timoutMilliseconds;
    this.searchDepth = searchDepth;
    this.gridDivider = gridSize;
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
    this.gs = new GameState(g, width, height);
    this.role = role;
  }

  @Override
  public GameMove maximizeCrossings(GameMove lastMove) {
    return moveWithTimeout(lastMove, true);
  }

  @Override
  public GameMove minimizeCrossings(GameMove lastMove) {

    return moveWithTimeout(lastMove, false);

  }

  @Override
  public GameMove maximizeCrossingAngles(GameMove lastMove) {
    return moveWithTimeout(lastMove, true);
  }

  @Override
  public GameMove minimizeCrossingAngles(GameMove lastMove) {
    return moveWithTimeout(lastMove, false);
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

  public GameMove moveWithTimeout(GameMove lastMove, boolean maximizeCrossings) {

    if (lastMove != null) {
      gs.applyMove(lastMove);
    }

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Your task to be executed
    Callable<GameMove> task = () -> {
      GameMove betterMove = generateMove(maximizeCrossings);
      gs.applyMove(betterMove);
      return betterMove;
    };

    // Submit the task to the executor
    Future<GameMove> future = executorService.submit(task);
    GameMove move = null;
    try {
      move = future.get(timoutMilliseconds, TimeUnit.MILLISECONDS);
    } catch (TimeoutException e) {
      if (bestMove != null) {
        move = bestMove;
      } else {
        System.out.println("Timeout, returning random move");
        move = getRandomGameMove();
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace(); // Handle other exceptions
    } finally {
      executorService.shutdown();
    }
    return move;

  }

  public ArrayList<Coordinate> getCoordinatesToTry(int width, int height,
      int maxPoint, GameState gs, int divider) {

    Random r = new Random();
    int intervalSizeX = width / divider;
    int intervalSizeY = height / divider;
    int maxRetriesForEachGridSection = 3;

    ArrayList<Coordinate> coords = new ArrayList<>();

    for (int i = intervalSizeX; i < intervalSizeX * divider; i += intervalSizeX) {
      for (int j = intervalSizeY; j < intervalSizeY * divider; j += intervalSizeY) {
        int x;
        int y;

        int numTries = 0;
        Coordinate c;
        do {
          x = i + r.nextInt(intervalSizeX);
          y = j + r.nextInt(intervalSizeY);
          c = new Coordinate(x, y);
          numTries++;
        } while (gs.getUsedCoordinates()[x][y] == 1 && numTries < maxRetriesForEachGridSection);
        coords.add(c);
      }
    }
    return coords;
  }

  private Vertex chooseNextVertexWithEdge(boolean maximize) {
    Vertex alternativeVertex = null;

    for (Vertex v : g.getVertices()) {
      if (!gs.getPlacedVertices().contains(v)) {
        var edges = g.getIncidentEdges(v);
        // choose Vertex with edges for maximization
        if (maximize) {
          if (edges.iterator().hasNext()) {
            return v;
          } else {
            alternativeVertex = v;
          }
          // // choose Vertex without edges for minimization
        } else {
          if (!edges.iterator().hasNext()) {
            return v;
          } else {
            alternativeVertex = v;
          }

        }
      }
    }
    return alternativeVertex;
  }

  private ScoredCoordinate generateBestCoordinate(Vertex v,
      boolean maximizeCrossings, int depth) {

    ArrayList<Coordinate> possibleCoordinates = getCoordinatesToTry(width, height,
        numberOfPositionsPerCell, gs, gridDivider);

    var scoredCoordinates = evaluatePositions(possibleCoordinates, v, maximizeCrossings, depth);

    ScoredCoordinate bestScoredCoordinate = scoredCoordinates.get(0);

    if (maximizeCrossings) {
      bestScoredCoordinate = Collections.max(scoredCoordinates, Comparator.comparing(s -> s.getScore()));
    } else {
      bestScoredCoordinate = Collections.min(scoredCoordinates, Comparator.comparing(s -> s.getScore()));
    }

    // if (maximizeCrossings) {
    // System.out.println("Maximizing crossings and found score: " +
    // bestScoredCoordinate.getScore());
    // } else {
    // System.out.println("Minimizing crossings and found score: " +
    // bestScoredCoordinate.getScore());
    // }

    return bestScoredCoordinate;
  }

  private GameMove generateMove(boolean maximizeCrossings) {

    Vertex v = chooseNextVertexWithEdge(maximizeCrossings);
    var bestCoordinate = generateBestCoordinate(v, maximizeCrossings, searchDepth);

    return new GameMove(v, bestCoordinate.getCoordinate());
  }

  private double evaluateMove(GameMove move, boolean maximizeCrossings, int depth) {
    double crossingNumberChange;
    if (this.role.equals(Role.MAX_ANGLE) || this.role.equals(Role.MIN_ANGLE)) {
      crossingNumberChange = calculateCrossingAngles(move);
    } else {
      crossingNumberChange = calculateNewEdgeCrossings(move);
      System.out.println("Crossings used");
    }
    if (depth == 0 || this.gs.getPlacedVertices().size() == this.g.getN()) {
      return crossingNumberChange;
    }
    applyMove(move);
    Vertex v = chooseNextVertexWithEdge(maximizeCrossings);
    if (v == null) {
      // fail safe
      return crossingNumberChange;
    }

    var toalCrossingNumberChange = crossingNumberChange
        + generateBestCoordinate(v, !maximizeCrossings, depth - 1).getScore();

    undoMove(move);
    return toalCrossingNumberChange;

  }

  private ArrayList<ScoredCoordinate> evaluatePositions(ArrayList<Coordinate> possibleCoordinates,
      Vertex vertexToBePlaced, boolean maximizeCrossings, int depth) {

    var scoredCoordinates = new ArrayList<ScoredCoordinate>();

    for (Coordinate c : possibleCoordinates) {
      var move = new GameMove(vertexToBePlaced, c);
      var score = evaluateMove(move, maximizeCrossings, depth);
      scoredCoordinates.add(new ScoredCoordinate(score, c));
    }
    return scoredCoordinates;
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

  private double calculateCrossingAngles(GameMove newMove) {

    applyMove(newMove);
    double result = 0;
    var adjacentEdges = g.getIncidentEdges(newMove.getVertex());
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
              result += Segment.squaredCosineOfAngle(s1, s2);
            }
          }
        }
      }
    }
    undoMove(newMove);
    return result;
  }

}
