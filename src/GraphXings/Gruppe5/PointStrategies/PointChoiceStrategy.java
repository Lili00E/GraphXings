package GraphXings.Gruppe5.PointStrategies;

import java.util.ArrayList;

import GraphXings.Data.Coordinate;
import GraphXings.Game.GameState;

public interface PointChoiceStrategy {

    public ArrayList<Coordinate> getCoordinatesToTry(int width, int height,
                                                     int maxPoints, GameState gs);

}
