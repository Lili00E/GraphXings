package GraphXings.Gruppe5.Models;

import GraphXings.Data.Coordinate;

public class ScoredCoordinate {

  public final double score;
  public final Coordinate coordinate;

  public double getScore() {
    return score;
  }

  public Coordinate getCoordinate() {
    return coordinate;
  }

  public ScoredCoordinate(double score, Coordinate coordinate) {
    this.score = score;
    this.coordinate = coordinate;
  }
}
