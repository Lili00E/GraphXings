package GraphXings.Gruppe5Algo.PointStrategies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Vertex;
import GraphXings.Gruppe5Algo.Models.HeatMap;

public class HeatMapChoiceStrategy implements PointChoiceStrategy {

    private HeatMap heatMap;
    private int maxPoints;

    public HeatMapChoiceStrategy(HeatMap map, int maxPoints) {
        this.heatMap = map;
        this.maxPoints = maxPoints;
    }

    public HeatMapChoiceStrategy(HeatMap map) {
        this.heatMap = map;
        this.maxPoints = 20;
    }

    private Coordinate getRandomUnusedCoord(Random r, int[][] usedCoordinates, int width, int height,
            HashSet<Vertex> placedVertices) {

        Coordinate c;
        int x, y;

        int stepWidth = width / heatMap.getWidth();
        int stepHeight = height / heatMap.getHeight();

        do {
            c = heatMap.chooseWeightedCoord();
            x = c.getX() * stepWidth + r.nextInt(stepWidth);
            y = c.getY() * stepHeight + r.nextInt(stepHeight);

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
