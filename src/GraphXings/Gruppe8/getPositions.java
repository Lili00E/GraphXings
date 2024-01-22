package GraphXings.Gruppe8;
import java.util.ArrayList;
import java.util.LinkedList;

import GraphXings.Data.Coordinate;
import java.util.List;
import java.util.Queue;



public class getPositions {
    public static Coordinate getNextFreeCoordinate(Coordinate inputCoordinate, int[][] usedCoordinates, int width, int height) {
        boolean[][] visited = new boolean[width][height];
        Queue<Coordinate> queue = new LinkedList<>();

        queue.add(inputCoordinate);
        visited[inputCoordinate.getX()][inputCoordinate.getY()] = true;

        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

        while (!queue.isEmpty()) {

            Coordinate current = queue.poll();

            if (usedCoordinates[current.getX()][current.getY()] == 0) {
                return current;
            }

            for (int i = 0; i < 4; i++) {
                int newX = current.getX() + dx[i];
                int newY = current.getY() + dy[i];

                if (newX >= 0 && newY >= 0 && newX < width && newY < height && !visited[newX][newY]) {
                    queue.add(new Coordinate(newX, newY));
                    visited[newX][newY] = true;
                }
            }
        }

        return null;
    }

}
