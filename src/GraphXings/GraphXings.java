package GraphXings;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Algorithms.NewRandomPlayer;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameInstance.ConstantGameInstanceFactory;
import GraphXings.Game.GameInstance.GameInstanceFactory;
import GraphXings.Game.Match.NewMatch;
import GraphXings.Game.Match.NewMatchResult;
import GraphXings.Game.NewGame;
import GraphXings.Game.NewGameResult;
import GraphXings.Gruppe5.GUI.GUIGame;
import GraphXings.Gruppe5.NewRandomChoicePlayer;

public class GraphXings {
    public static void main(String[] args) {

        int NumNodes = 100;
        int numGames = 10;
        int width = 1000;
        int height = 1000;
        int samplePointsPerMove = 10;
        int timeoutInMilliseconds = 27000;

        Graph g = inializeGraph(NumNodes);
        NewPlayer player1 = new NewRandomChoicePlayer("Basic Crossing with Random Choice", samplePointsPerMove, timeoutInMilliseconds);
        NewPlayer player2 = new NewRandomPlayer("Random Player");
        GameInstanceFactory gi = new ConstantGameInstanceFactory(g, width, height);

        var startTime = System.currentTimeMillis();

//        NewGame game = new NewGame(g, width, height, player1, player2);
//        NewGameResult result = game.play();

//         //TODO fix GUIGame
//         new GUIGame(g, 1000, 1000, player1, player2);
//         var GUIgame = new GUIGame(g, 1000, 1000, player1, player2);
//         GUIgame.playFullGame();

        NewMatch match = new NewMatch(player1, player2, gi, numGames);
        NewMatchResult result = match.play();
        result.announceResult();
        System.out.println("" + result.getPlayer1().getName() + ": " + result.getGamesWon1() + ", " + result.getPlayer2().getName() + ": " + result.getGamesWon2());

        var endTime = System.currentTimeMillis();
        System.out.println("Runtime was " + (endTime - startTime) + "ms");

    }

    private static Graph inializeGraph(int NumNodes) {
        Vertex first = null;
        Vertex last = null;

        Graph g = new Graph();
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
        return g;
    }
}
