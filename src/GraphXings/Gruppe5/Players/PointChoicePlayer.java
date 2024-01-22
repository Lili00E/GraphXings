package GraphXings.Gruppe5.Players;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Data.*;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameState;
import GraphXings.Gruppe5.PointStrategies.PointChoiceStrategy;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;

public class PointChoicePlayer implements NewPlayer {

  private String name;

  private Graph g;
  private GameState gs;

  private int width;
  private int height;
  private Role role;

  private PointChoiceStrategy minPointChoiceStrategy;
  private PointChoiceStrategy maxPointChoiceStrategy;

  private int maxPoints;

  private GameMove ourNewGameMove;

  public PointChoicePlayer(String name, PointChoiceStrategy minStrategy, PointChoiceStrategy maxStrategy) {
    this.name = name;
    this.maxPointChoiceStrategy = maxStrategy;
    this.minPointChoiceStrategy = minStrategy;
    this.maxPoints = 0;
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
    if (this.maxPoints == 0) {
      this.maxPoints = setMaxPoints();
    }
    this.role = role;
    this.ourNewGameMove = getRandomGameMove();
  }

  private int setMaxPoints() {
    int numNodes = this.g.getN();
    if (numNodes <= 100) {
      return 50;
    } else if (numNodes <= 500) {
      return 20;
    } else if (numNodes <= 1000) {
      return 10;
    } else if (numNodes <= 7000) {
      return 5;
    } else {
      return 2;
    }
  }

  @Override
  public GameMove maximizeCrossings(GameMove lastMove) {
    findMoveWithTimeout(lastMove, true);
    return this.ourNewGameMove;
  }

  @Override
  public GameMove minimizeCrossings(GameMove lastMove) {
    findMoveWithTimeout(lastMove, false);
    return this.ourNewGameMove;
  }

  @Override
  public GameMove maximizeCrossingAngles(GameMove lastMove) {
    findMoveWithTimeout(lastMove, true);
    return this.ourNewGameMove;
  }

  @Override
  public GameMove minimizeCrossingAngles(GameMove lastMove) {
    findMoveWithTimeout(lastMove, false);
    return this.ourNewGameMove;
  }

  private GameMove getRandomGameMove() {
    Random r = new Random();
    int stillToBePlaced = g.getN() - gs.getPlacedVertices().size();
    if (stillToBePlaced == 0) {
      throw new RuntimeException("All vertices are placed");
    }

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

  public void findMoveWithTimeout(GameMove lastMove, boolean maximizeCrossings) {

    if (lastMove != null) {
      gs.applyMove(lastMove);
    }

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Task to be executed
    Callable<GameMove> task = () -> {
      findMove(maximizeCrossings);
      gs.applyMove(this.ourNewGameMove);
      return this.ourNewGameMove;
    };

    // Submit task to the executor
    Future<GameMove> future = executorService.submit(task);
    try {
      final var maxTimeoutMs = 240000;
      var calculatedTimeout = maxTimeoutMs/(g.getN()/2);
      future.get(calculatedTimeout, TimeUnit.MILLISECONDS); // 2 seconds timeout
    } catch (TimeoutException ignored) {
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace(); // Handle other exceptions
    } finally {
      executorService.shutdown();
    }

  }

  private Vertex chooseNextVertexWithEdge(boolean maximize) {
    Vertex alternativeVertex = null;

    if (g.getN() == gs.getPlacedVertices().size()) {

      for (Vertex v : gs.getPlacedVertices()) {
        System.out.println(v.toString());
      }
      throw new RuntimeException("All vertices are placed");
    }

    for (Vertex v : g.getVertices()) {
      if (!gs.getPlacedVertices().contains(v)) {
        var edges = g.getIncidentEdges(v);
        // choose Vertex with edges for maximization
        alternativeVertex = v;
        if (maximize) {
          if (edges.iterator().hasNext()) {
            return v;
          } // // choose Vertex without edges for minimization
        } else {
          if (!edges.iterator().hasNext()) {
            return v;
          }
        }
      }
    }
    if (alternativeVertex == null) {
      throw new RuntimeException("No alternative vertex found");
    }
    return alternativeVertex;
  }

  private void findMove(boolean maximizeCrossings) {

    Vertex v = chooseNextVertexWithEdge(maximizeCrossings);

    if (maximizeCrossings) {
      var randomCoords = maxPointChoiceStrategy.getCoordinatesToTry(width, height,
          maxPoints, this.gs);
      var c = getBestOfPlacement(randomCoords, v);
      this.ourNewGameMove = new GameMove(v, c);
    } else {
      var randomCoords = minPointChoiceStrategy.getCoordinatesToTry(width, height,
          maxPoints, this.gs);
      var c = getWorstOfPlacement(randomCoords, v);
      this.ourNewGameMove = new GameMove(v, c);
    }

  }

  private Coordinate getBestOfPlacement(ArrayList<Coordinate> possibleCoordinates, Vertex vertexToBePlaced) {

    Coordinate coordinateWithMaxCross = possibleCoordinates.get(0);
    double max = 0;

    for (Coordinate c : possibleCoordinates) {

      var move = new GameMove(vertexToBePlaced, c);
      double currentCrossingNum;
      if (this.role.equals(Role.MAX_ANGLE) || this.role.equals(Role.MIN_ANGLE)) {
        currentCrossingNum = calculateCrossingAngles(move);
      } else {
        currentCrossingNum = calculateNewEdgeCrossings(move);
      }

      if (currentCrossingNum >= max) {
        max = currentCrossingNum;
        coordinateWithMaxCross = c;
        this.ourNewGameMove = move;
      }
    }
    return coordinateWithMaxCross;
  }

  private Coordinate getWorstOfPlacement(ArrayList<Coordinate> possibleCoordinates,
      Vertex vertexToBePlaced) {

    Coordinate coordinateWithMinCross = possibleCoordinates.get(0);
    double min = Double.MAX_VALUE;

    for (Coordinate c : possibleCoordinates) {

      var move = new GameMove(vertexToBePlaced, c);
      double currentCrossingNum;

      if (this.role.equals(Role.MAX_ANGLE) || this.role.equals(Role.MIN_ANGLE)) {
        currentCrossingNum = calculateCrossingAngles(move);
      } else {
        currentCrossingNum = calculateNewEdgeCrossings(move);
      }
      if (currentCrossingNum < min) {
        min = currentCrossingNum;
        coordinateWithMinCross = c;
        this.ourNewGameMove = move;
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

  private double calculateNewEdgeCrossings(GameMove newMove) {

    applyMove(newMove);
    double crossingNumber = 0;
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

            var v1 = e1.getS();
            var v2 = e1.getT();
            var v3 = e2.getS();
            var v4 = e2.getT();

            var c1 = vertexCoordinates.get(v1);
            var c2 = vertexCoordinates.get(v2);
            var c3 = vertexCoordinates.get(v3);
            var c4 = vertexCoordinates.get(v4);

            if (c1 == null || c2 == null || c3 == null || c4 == null) {
              continue;
            }

            Segment s1 = new Segment(c1, c2);
            Segment s2 = new Segment(c3, c4);
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
