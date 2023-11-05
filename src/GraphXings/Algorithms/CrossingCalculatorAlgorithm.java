package GraphXings.Algorithms;

import java.util.HashMap;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;

public interface CrossingCalculatorAlgorithm {

    public int computeCrossingNumber(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates);

}
