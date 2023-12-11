package GraphXings.Gruppe5Algo.PointStrategies;

import java.util.ArrayList;
import java.util.HashSet;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Vertex;

public interface PointChoiceStrategy {

    public ArrayList<Coordinate> getCoordinatesToTry(int[][] usedCoordinates, int width, int height,
            HashSet<Vertex> placedVertices, int maxPoints);

}
