package GraphXings;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Algorithms.NewRandomPlayer;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameInstance.GameInstanceFactory;
import GraphXings.Game.GameInstance.PlanarGameInstanceFactory;
import GraphXings.Game.League.NewLeague;
import GraphXings.Game.League.NewLeagueResult;
import GraphXings.Game.Match.NewMatch;
import GraphXings.Game.Match.NewMatchResult;
import GraphXings.Gruppe5.Models.HeatMapFileReader;
import GraphXings.Gruppe5.Players.PointChoicePlayer;
import GraphXings.Gruppe5.Players.PointChoicePlayerNewTimeout;
import GraphXings.Gruppe5.Players.RecursiveSearchPlayer;
import GraphXings.Gruppe5.PointStrategies.HeatMapChoiceStrategy;
import GraphXings.Gruppe5.PointStrategies.RandomPointChoiceStrategy;
import GraphXings.Gruppe8.EfficientWinningPlayer;

public class GraphXings {
    public static void main(String[] args) throws FileNotFoundException {
        var smallHeatMapMin = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/SmallHeatMapMin.txt");
        var smallHeatMapMax = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/SmallHeatMapMax.txt");

        ArrayList<NewPlayer> players = new ArrayList<>();
//        players.add(new NewRandomPlayer("R1"));
//        players.add(new PointChoicePlayer("RC 100", new RandomPointChoiceStrategy(100),
//                new RandomPointChoiceStrategy(100),
//                2000));
//        players.add(new RecursiveSearchPlayer("My Player: Recursive Search", 5, 100, 100, 20000));
//        players.add(new PointChoicePlayer("My Player", new HeatMapChoiceStrategy(smallHeatMapMin),
//                new HeatMapChoiceStrategy(smallHeatMapMax), 20000));
//        players.add(new GraphXings.Gruppe8.EfficientWinningPlayer("Gruppe 8"));

//        players.add(new PointChoicePlayer("My Player: Old Timeout", new HeatMapChoiceStrategy(smallHeatMapMin),
//                new HeatMapChoiceStrategy(smallHeatMapMax), 20000));
        players.add(new PointChoicePlayerNewTimeout("My Player", new HeatMapChoiceStrategy(smallHeatMapMin),
                new HeatMapChoiceStrategy(smallHeatMapMax), 20000));
        players.add(new EfficientWinningPlayer("Gruppe 8"));
//        players.add(new RecursiveSearchPlayer("Recursive Search", 5, 100, 100, 20000));
        long timeLimit = 300000000000l;
        long seed = 23071983;
        int bestOf = 3;
        NewMatch.MatchType matchType = NewMatch.MatchType.CROSSING_ANGLE;
        PlanarGameInstanceFactory factory = new PlanarGameInstanceFactory(seed);
        runLeague(players,bestOf,timeLimit,factory,matchType,seed);
        //runRemainingMatches(player,players,bestOf,timeLimit,factory);

//        int numGames = 100;
//        int NumNodes = 10;
//        int width = 100;
//        int height = 100;
//        int samplePointsPerMove = 20;
//        int timeoutInMilliseconds = 1000;
//
//        Graph g = inializeGraph(NumNodes);
//
//        HeatMap minHeatMap, maxHeatMap;
//        try {
//            maxHeatMap = new HeatMapFileReader()
//                    .readFromFile("./GraphXings/Gruppe5Algo/PointStrategies/HeatMaps/SimpleHeatMap.txt");
//            minHeatMap = new HeatMapFileReader()
//                    .readFromFile("./GraphXings/Gruppe5Algo/PointStrategies/HeatMaps/UniformHeatMap.txt");
//        } catch (FileNotFoundException e) {
//            return;
//
//        }
//
//        var myPlayer = new PointChoicePlayer("My Player", new HeatMapChoiceStrategy(minHeatMap),
//                new HeatMapChoiceStrategy(maxHeatMap), 2000);
//        NewPlayer player1 = myPlayer;
//        NewPlayer player2 = new NewRandomPlayer("Random Player");
//        // NewPlayer player1 = new RandomChoicePlayerOld("My Old Player",
//        // samplePointsPerMove, timeoutInMilliseconds);
//        //GameInstanceFactory gi = new ConstantGameInstanceFactory(g, width, height);
//        GameInstanceFactory gi = new RandomCycleFactory(12060351, true);
//
//        var startTime = System.currentTimeMillis();
//
//        // NewGame game = new NewGame(g, width, height, player1, player2);
//        // NewGameResult result = game.play();
//
//        NewMatch match = new NewMatch(player1, player2, gi, numGames);
//        NewMatchResult result = match.play();
//        var endTime = System.currentTimeMillis();
//        System.out.println(result.announceResult());
//
//        System.out.println("Runtime was " + (endTime - startTime) + "ms");
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
    public static void runLeague(ArrayList<NewPlayer> players, int bestOf, long timeLimit, GameInstanceFactory factory, NewMatch.MatchType matchType, long seed)
    {
        NewLeague l = new NewLeague(players,bestOf,timeLimit,factory,matchType,seed);
        NewLeagueResult lr = l.runLeague();
        System.out.println(lr.announceResults());
    }
    public static void runRemainingMatches(NewPlayer p1, ArrayList<NewPlayer> opponents, int bestOf, long timeLimit, GameInstanceFactory factory, NewMatch.MatchType matchType, long seed)
    {
        int i = 1;
        for (NewPlayer opponent : opponents)
        {
            NewMatch m = new NewMatch(p1,opponent,factory,bestOf,timeLimit,matchType,seed);
            NewMatchResult mr = m.play();
            System.out.println("Match " + i++ + ": " + mr.announceResult());
        }
    }
}