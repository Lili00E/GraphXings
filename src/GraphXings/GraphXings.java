package GraphXings;

import java.io.FileNotFoundException;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Algorithms.NewRandomPlayer;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameInstance.ConstantGameInstanceFactory;
import GraphXings.Game.GameInstance.GameInstanceFactory;
import GraphXings.Game.GameInstance.RandomCycleFactory;
import GraphXings.Game.Match.NewMatch;
import GraphXings.Game.Match.NewMatchResult;
import GraphXings.Game.NewGame;
import GraphXings.Game.NewGameResult;
import GraphXings.Gruppe5Algo.Models.HeatMap;
import GraphXings.Gruppe5Algo.Models.HeatMapFileReader;
import GraphXings.Gruppe5Algo.Players.PointChoicePlayer;
import GraphXings.Gruppe5Algo.PointStrategies.HeatMapChoiceStrategy;

public class GraphXings {
    public static void main(String[] args) {

        int numGames = 100;
        int NumNodes = 10;
        int width = 100;
        int height = 100;
        int samplePointsPerMove = 20;
        int timeoutInMilliseconds = 1000;

        Graph g = inializeGraph(NumNodes);

        HeatMap minHeatMap, maxHeatMap;
        try {
            maxHeatMap = new HeatMapFileReader()
                    .readFromFile("./GraphXings/Gruppe5Algo/PointStrategies/HeatMaps/SimpleHeatMap.txt");
            minHeatMap = new HeatMapFileReader()
                    .readFromFile("./GraphXings/Gruppe5Algo/PointStrategies/HeatMaps/UniformHeatMap.txt");
        } catch (FileNotFoundException e) {
            return;

        }

        var myPlayer = new PointChoicePlayer("My Player", new HeatMapChoiceStrategy(minHeatMap),
                new HeatMapChoiceStrategy(maxHeatMap), 2000);
        NewPlayer player1 = myPlayer;
        NewPlayer player2 = new NewRandomPlayer("Random Player");
        // NewPlayer player1 = new RandomChoicePlayerOld("My Old Player",
        // samplePointsPerMove, timeoutInMilliseconds);
        //GameInstanceFactory gi = new ConstantGameInstanceFactory(g, width, height);
        GameInstanceFactory gi = new RandomCycleFactory(12060351, true);

        var startTime = System.currentTimeMillis();

        // NewGame game = new NewGame(g, width, height, player1, player2);
        // NewGameResult result = game.play();

        NewMatch match = new NewMatch(player1, player2, gi, numGames);
        NewMatchResult result = match.play();
        var endTime = System.currentTimeMillis();
        System.out.println(result.announceResult());

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
