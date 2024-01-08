package GraphXings.Gruppe5.PointStrategies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameState;
import GraphXings.Gruppe5.Models.HeatMap;

public class HeatMapChoiceStrategy implements PointChoiceStrategy {

    private HeatMap heatMap;

    public HeatMapChoiceStrategy(HeatMap map) {
        this.heatMap = map;
    }

    private Coordinate getRandomUnusedCoord(Random r, int[][] usedCoordinates, int width, int height,
            HashSet<Vertex> placedVertices) {

        Coordinate c;
        int x, y;

        float stepWidth = (float) width / (float) heatMap.getWidth();
        float stepHeight = (float) height / (float) heatMap.getHeight();

        do {
            c = heatMap.chooseWeightedCoord();
            if (stepWidth > 1) {
                x = c.getX() * (int) stepWidth + r.nextInt((int) stepWidth);
            } else {
                x = (int) ((float) c.getX() * stepWidth);
            }
            if (stepHeight > 1) {
                y = c.getY() * (int) stepHeight + r.nextInt((int) stepHeight);

            } else {
                y = (int) ((float) c.getY() * stepHeight);
            }

        } while (usedCoordinates[x][y] != 0);

        return new Coordinate(x, y);

    }

    @Override
    public ArrayList<Coordinate> getCoordinatesToTry(int width, int height, int maxPoints, GameState gs) {
        Random r = new Random();
        var randomCoords = new ArrayList<Coordinate>();

        int maxAvailableCoords = (width * height) - gs.getPlacedVertices().size();
        int numPoints = Math.min(maxAvailableCoords, maxPoints);

        for (var i = 0; i < numPoints; i++) {
            var newCoord = getRandomUnusedCoord(r, gs.getUsedCoordinates(), width, height, gs.getPlacedVertices());
            randomCoords.add(newCoord);
        }
        return randomCoords;
    }

}
