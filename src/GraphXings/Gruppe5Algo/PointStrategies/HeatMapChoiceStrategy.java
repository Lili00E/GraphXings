package GraphXings.Gruppe5Algo.PointStrategies;

import java.util.ArrayList;
import java.util.Random;

import GraphXings.Data.Coordinate;
import GraphXings.Game.GameState;
import GraphXings.Gruppe5Algo.Models.HeatMap;

public class HeatMapChoiceStrategy implements PointChoiceStrategy {

    private HeatMap heatMap;

    public HeatMapChoiceStrategy(HeatMap map) {
        this.heatMap = map;
    }

    private Coordinate getRandomUnusedCoord(Random r, GameState gs, int height, int width) {

        Coordinate c;
        int x, y;

        int stepWidth = width / heatMap.getWidth();
        int stepHeight = height / heatMap.getHeight();

        do {
            c = heatMap.chooseWeightedCoord();
            x = c.getX() * stepWidth + r.nextInt(stepWidth);
            y = c.getY() * stepHeight + r.nextInt(stepHeight);

        } while (gs.getUsedCoordinates()[x][y] != 0);

        return new Coordinate(x, y);

    }

    @Override
    public ArrayList<Coordinate> getCoordinatesToTry(int width, int height,
                                                     int maxPoints, GameState gs) {
        Random r = new Random();
        var randomCoords = new ArrayList<Coordinate>();

        int maxAvailableCoords = (width * height) - gs.getPlacedVertices().size();
        int numPoints = Math.min(maxAvailableCoords, maxPoints);

        for (var i = 0; i < numPoints; i++) {
            var newCoord = getRandomUnusedCoord(r,gs, height, width);
            randomCoords.add(newCoord);
        }
        return randomCoords;
    }

}
