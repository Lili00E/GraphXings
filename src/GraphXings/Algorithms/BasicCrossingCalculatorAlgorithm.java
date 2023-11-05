package GraphXings.Algorithms;

import java.util.HashMap;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;

public class BasicCrossingCalculatorAlgorithm implements CrossingCalculatorAlgorithm {

    @Override
    public int computeCrossingNumber(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates) {
        var cc = new CrossingCalculator(g, vertexCoordinates);
        return cc.computeCrossingNumber();
    }

}
