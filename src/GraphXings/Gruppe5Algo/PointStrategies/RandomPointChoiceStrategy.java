package GraphXings.Gruppe5Algo.PointStrategies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Vertex;

public class RandomPointChoiceStrategy implements PointChoiceStrategy {

    private int maxPoints;

    public RandomPointChoiceStrategy(int numPoints) {
        this.maxPoints = numPoints;
    }

    private Coordinate getRandomUnusedCoord(Random r, int[][] usedCoordinates, int width, int height,
            HashSet<Vertex> placedVertices) {

        var x = r.nextInt(width);
        var y = r.nextInt(height);
        do {
            x = r.nextInt(width);
            y = r.nextInt(height);
        } while (usedCoordinates[x][y] != 0);

        return new Coordinate(x, y);

    }

    @Override
    public ArrayList<Coordinate> getCoordinatesToTry(int[][] usedCoordinates, int width, int height,
            HashSet<Vertex> placedVertices) {
        Random r = new Random();
        var randomCoords = new ArrayList<Coordinate>();

        int maxAvailableCoords = (width * height) - placedVertices.size();
        int numPoints = Math.min(maxAvailableCoords, maxPoints);

        for (var i = 0; i < numPoints; i++) {
            var newCoord = getRandomUnusedCoord(r, usedCoordinates, width, height, placedVertices);
            randomCoords.add(newCoord);
        }
        return randomCoords;
    }
}