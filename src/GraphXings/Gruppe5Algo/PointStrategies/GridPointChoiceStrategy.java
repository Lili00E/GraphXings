package GraphXings.Gruppe5Algo.PointStrategies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Vertex;

public class GridPointChoiceStrategy implements PointChoiceStrategy {

    private int divider;

    public GridPointChoiceStrategy(int divider) {
        this.divider = divider;
    }

    @Override
    public ArrayList<Coordinate> getCoordinatesToTry(int[][] usedCoordinates, int width, int height,
            HashSet<Vertex> placedVertices) {

        Random r = new Random();
        int intervalSizeX = width / divider;
        int intervalSizeY = height / divider;
        int maxRetriesForEachGridSection = 3;

        ArrayList<Coordinate> coords = new ArrayList<>();

        for (int i = intervalSizeX; i < width; i += intervalSizeX) {
            for (int j = intervalSizeY; j < height; j += intervalSizeY) {
                int x;
                int y;
                int numTries = 0;
                Coordinate c;
                do {
                    x = i + r.nextInt(intervalSizeX);
                    y = j + r.nextInt(intervalSizeY);
                    c = new Coordinate(x, y);
                    numTries++;
                } while (usedCoordinates[x][y] == 1 && numTries < maxRetriesForEachGridSection);
                coords.add(c);
            }
        }
        return coords;
    }

}
