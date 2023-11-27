package GraphXings.Competitors.Group08;
import java.util.ArrayList;
import GraphXings.Data.Coordinate;
import java.util.List;



public class getPositions {
    public static List<Coordinate> generateNeighbors(Coordinate inputCoordinate, int range) {
        List<Coordinate> neighbors = new ArrayList<>();

        for (int i = 1; i <= range; i++) {
            Coordinate ol = new Coordinate(inputCoordinate.getX() - i, inputCoordinate.getY() + i);
            Coordinate or = new Coordinate(inputCoordinate.getX() + i, inputCoordinate.getY() + i);
            Coordinate ul = new Coordinate(inputCoordinate.getX() - i, inputCoordinate.getY() - i);
            Coordinate ur = new Coordinate(inputCoordinate.getX() + i, inputCoordinate.getY() - i);

            neighbors.addAll(getAllCoordinates(ol, or, ul, ur));
        }

        return neighbors;
    }

    private static List<Coordinate> getAllCoordinates(Coordinate ol, Coordinate or, Coordinate ul, Coordinate ur) {
        List<Coordinate> allCoordinates = new ArrayList<>();
        for (int x = ol.getX(); x <= or.getX(); x++) {
            for (int y = ul.getY(); y <= ol.getY(); y++) {
                allCoordinates.add(new Coordinate(x, y));
            }
        }
        return allCoordinates;
    }

    private static List<Coordinate> addNeighborIfValid(List<Coordinate> neighbors, int width, int height) {
        List<Coordinate> inBounds = new ArrayList<>();
        for (Coordinate neighbor : neighbors){

            if (neighbor.getX() >= 0 && neighbor.getX() < width && neighbor.getY() >= 0 && neighbor.getY() < height) {
                if (!inBounds.contains(neighbor)) {
                    inBounds.add(neighbor);
                }
            }
        }
        return inBounds;
    }

    public static void main(String[] args) {
        int range = 10;
        int width = 100;
        int height = 100; 
        Coordinate inputCoordinate = new Coordinate(0, 0);
        List<Coordinate> neighborCoordinates = generateNeighbors(inputCoordinate, range);
        List<Coordinate> neighbCoordinates = addNeighborIfValid(neighborCoordinates, width, height);
        System.out.println("Input Coordinate: " + inputCoordinate);
        System.out.println("Neighbor Coordinates within bounds:");
        for (Coordinate neighbor : neighbCoordinates) {
            System.out.println(neighbor);
        }
    }
}
