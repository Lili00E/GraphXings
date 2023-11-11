package GraphXings.Gruppe5;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;

import java.util.HashMap;

public interface CrossingCalculatorAlgorithm {

    public int computeCrossingNumber(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates);

}
