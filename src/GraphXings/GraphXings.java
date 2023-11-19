package GraphXings;

//import GraphXings.Algorithms.BasicCrossingCalculatorAlgorithm;
import GraphXings.Algorithms.*;
//import GraphXings.Algorithms.RandomChoicePlayer;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.NewGame;
import GraphXings.Game.NewGameResult;
import GraphXings.Gruppe5.NewRandomChoicePlayer;
import GraphXings.Gruppe5.BasicCrossingCalculatorAlgorithm;

public class GraphXings {
    public static void main(String[] args) {
        // Create a graph g. This time it is a 10-cycle!

        int NumNodes = 100;
        Vertex first = null;
        Vertex last = null;

        Graph g = new Graph();
        final long timeStart = System.currentTimeMillis();
        for (int i = 0; i < NumNodes; i++) {
            Vertex v = new Vertex((i + 1) + "_Node");
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
            last = v;
        }
        final long timeEnd = System.currentTimeMillis();
        System.out.println("InitializeVertices: " + (timeEnd - timeStart) + " Millisek.");

        int numGames = 1;
        int width = 10;
        int height = 10;

        int winScore = 0;
        int winsPlayer1 = 0;
        int winsPlayer2 = 0;
        BasicCrossingCalculatorAlgorithm cc = new BasicCrossingCalculatorAlgorithm();

//        var player1 = new RandomChoicePlayer("Basic Crossing with Random Choice", 20, cc, 27000);
//        var player2 = new RandomPlayer("Random Player");
        NewPlayer player1 = new NewRandomChoicePlayer("Basic Crossing with Random Choice", 20, 27000);
        NewPlayer player2 = new NewRandomPlayer("Random Player");

        var startTime = System.currentTimeMillis();

        NewGame game = new NewGame(g, width, height, player1, player2);
        NewGameResult result = game.play();
        result.announceResult();

//        for (var i = 0; i < numGames; i++) {
//            Game game = new Game(g, 10000, 10000, player1, player2);
//            GameResult res = game.play();
//            int gameWinScore = res.getWinScore();
//            winScore += gameWinScore;
//            if (gameWinScore > 0) {
//                winsPlayer1 += 1;
//            } else if (gameWinScore < 0) {
//                winsPlayer2 += 1;
//            }
//
//        }
        // new GUIGame(g, 1000, 1000, player1, player2);

        // var game = new GUIGame(g, 1000, 1000, player1, player2);

        // game.initGameRound();
        var endTime = System.currentTimeMillis();
        System.out.println("runtime was " + (endTime - startTime) + "ms");
//        System.out.println(winScore);
//        System.out.println(
//                player1.getName() + " won (" + winsPlayer1 + "), " + player2.getName() + " won (" + winsPlayer2 + ")");

    }
}
