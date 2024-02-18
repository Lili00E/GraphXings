package GraphXings;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

import GraphXings.Algorithms.NewPlayer;
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
import GraphXings.Gruppe5.Players.PointChoicePlayer;
import GraphXings.Gruppe5.PointStrategies.HeatMapChoiceStrategy;
import stuffUnused.Gruppe8.EfficientWinningPlayer;

public class GraphXings {
    public static void main(String[] args) throws FileNotFoundException {
        var smallHeatMapMin = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/SmallHeatMapMin.txt");
        var smallHeatMapMax = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/SmallHeatMapMax.txt");

        ArrayList<NewPlayer> players = new ArrayList<>();
        players.add(new PointChoicePlayer("My Player", new HeatMapChoiceStrategy(smallHeatMapMin),
                new HeatMapChoiceStrategy(smallHeatMapMax)));
        players.add(new EfficientWinningPlayer("Gruppe 8"));
        // players.add(new RecursiveSearchPlayer("Recursive Search", 5, 100, 100,
        // 20000));
        long timeLimit = 300000000000l;
        long seed = 27081883;
        int bestOf = 3;
        NewMatch.MatchType matchType = NewMatch.MatchType.CROSSING_ANGLE;
        PlanarGameInstanceFactory factory = new PlanarGameInstanceFactory(seed);
        System.out.println("Generated game with " + factory.getGameInstance().getG().getN() + "x"
                + factory.getGameInstance().getWidth() + "x"
                + factory.getGameInstance().getHeight());
        runLeague(players, bestOf, timeLimit, factory, matchType, seed);
        // runRemainingMatches(player,players,bestOf,timeLimit,factory);
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

    public static void runLeague(ArrayList<NewPlayer> players, int bestOf, long timeLimit, GameInstanceFactory factory,
            NewMatch.MatchType matchType, long seed) {
        NewLeague l = new NewLeague(players, bestOf, timeLimit, factory, matchType, seed);
        NewLeagueResult lr = l.runLeague();
        System.out.println(lr.announceResults());
    }

    public static void runRemainingMatches(NewPlayer p1, ArrayList<NewPlayer> opponents, int bestOf, long timeLimit,
            GameInstanceFactory factory, NewMatch.MatchType matchType, long seed) {
        int i = 1;
        for (NewPlayer opponent : opponents) {
            NewMatch m = new NewMatch(p1, opponent, factory, bestOf, timeLimit, matchType, seed);
            NewMatchResult mr = m.play();
            System.out.println("Match " + i++ + ": " + mr.announceResult());
        }
    }
}