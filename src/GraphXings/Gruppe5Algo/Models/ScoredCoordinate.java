package GraphXings.Gruppe5Algo.Models;

import GraphXings.Data.Coordinate;

public class ScoredCoordinate {

  public final int score;
  public final Coordinate coordinate;

  public Integer getScore() {
    return score;
  }

  public Coordinate getCoordinate() {
    return coordinate;
  }

  public ScoredCoordinate(int score, Coordinate coordinate) {
    this.score = score;
    this.coordinate = coordinate;
  }
}
