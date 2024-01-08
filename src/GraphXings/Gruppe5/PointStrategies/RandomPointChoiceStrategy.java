package GraphXings.Gruppe5.PointStrategies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameState;

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
    public ArrayList<Coordinate> getCoordinatesToTry(int width, int height,
                                                     int maxPoints, GameState gs) {
        Random r = new Random();
        var randomCoords = new ArrayList<Coordinate>();

        int maxAvailableCoords = (width * height) - gs.getPlacedVertices().size();
        int numPoints = Math.min(maxAvailableCoords, this.maxPoints);

        for (var i = 0; i < numPoints; i++) {
            var newCoord = getRandomUnusedCoord(r, gs.getUsedCoordinates(), width, height, gs.getPlacedVertices());
            randomCoords.add(newCoord);
        }
        return randomCoords;
    }
}