package GraphXings;

import GraphXings.Algorithms.BasicCrossingCalculatorAlgorithm;
import GraphXings.Algorithms.BentleyOttmannCrossingCalculator;
import GraphXings.Algorithms.RandomChoicePlayer;
import GraphXings.Algorithms.RandomPlayer;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.Game;
import GraphXings.Game.GameResult;

public class GraphXings {
    public static void main(String[] args) {
        // Create a graph g. This time it is a 10-cycle!

        int NumNodes = 1000;
        Vertex first = null;
        Vertex last = null;

        Graph g = new Graph();
        final long timeStart = System.currentTimeMillis();
        for (int i = 0; i < NumNodes; i++) {
            Vertex v = new Vertex((i + 1) + "");
            g.addVertex(v);
            if (i == 0) {
                first = v;
                last = v;
            } else if (i == (NumNodes - 1)) {
                Edge e = new Edge(v, first);
                g.addEdge(e);
            } else {
                Edge e = new Edge(last, v);
                g.addEdge(e);
            }
        }
        final long timeEnd = System.currentTimeMillis();
        System.out.println("InitializeVertices: " + (timeEnd - timeStart) + " Millisek.");
//
//        Vertex v1 = new Vertex("1");
//        Vertex v2 = new Vertex("2");
//        Vertex v3 = new Vertex("3");
//        Vertex v4 = new Vertex("4");
//        Vertex v5 = new Vertex("5");
//        Vertex v6 = new Vertex("6");
//        Vertex v7 = new Vertex("7");
//        Vertex v8 = new Vertex("8");
//        Vertex v9 = new Vertex("9");
//        Vertex v10 = new Vertex("10");
//        g.addVertex(v1);
//        g.addVertex(v2);
//        g.addVertex(v3);
//        g.addVertex(v4);
//        g.addVertex(v5);
//        g.addVertex(v6);
//        g.addVertex(v7);
//        g.addVertex(v8);
//        g.addVertex(v9);
//        g.addVertex(v10);
//        Edge e1 = new Edge(v1, v2);
//        Edge e2 = new Edge(v2, v3);
//        Edge e3 = new Edge(v3, v4);
//        Edge e4 = new Edge(v4, v5);
//        Edge e5 = new Edge(v5, v6);
//        Edge e6 = new Edge(v6, v7);
//        Edge e7 = new Edge(v7, v8);
//        Edge e8 = new Edge(v8, v9);
//        Edge e9 = new Edge(v9, v10);
//        Edge e10 = new Edge(v10, v1);
//        g.addEdge(e1);
//        g.addEdge(e2);
//        g.addEdge(e3);
//        g.addEdge(e4);
//        g.addEdge(e5);
//        g.addEdge(e6);
//        g.addEdge(e7);
//        g.addEdge(e8);
//        g.addEdge(e9);
//        g.addEdge(e10);
        // Run the game with two players.

        int numGames = 1;
        int winScore = 0;
        int winsPlayer1 = 0;
        int winsPlayer2 = 0;

        var player1 = new RandomPlayer("Random Player");
        // var player1 = new BetterPlayer("Better Player");
        var player2 = new RandomChoicePlayer("Better Random Player 2", 2, new BasicCrossingCalculatorAlgorithm());
        var startTime = System.currentTimeMillis();
        for (var i = 0; i < numGames; i++) {
            Game game = new Game(g, 10000, 10000, player1, player2);
            GameResult res = game.play();
            int gameWinScore = res.getWinScore();
            winScore += gameWinScore;
            if (gameWinScore > 0) {
                winsPlayer1 += 1;
            } else if (gameWinScore < 0) {
                winsPlayer2 += 1;
            }

        }
        var endTime = System.currentTimeMillis();
        System.out.println("runtime was " + (endTime - startTime) + "ms");
        System.out.println(winScore);
        System.out.println(
                player1.getName() + " won (" + winsPlayer1 + "), " + player2.getName() + " won (" + winsPlayer2 + ")");

    }
}
