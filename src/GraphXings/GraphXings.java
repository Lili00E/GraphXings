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
import GraphXings.Algorithms.NewRandomPlayer;
import GraphXings.Game.GameInstance.*;
import GraphXings.Game.League.NewLeague;
import GraphXings.Game.League.NewLeagueResult;
import GraphXings.Game.Match.NewMatch;
import GraphXings.Game.Match.NewMatchResult;
import java.util.ArrayList;

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

public class GraphXings
{
    public static void main (String[] args)
    {
        ArrayList<NewPlayer> players = new ArrayList<>();
        players.add(new NewRandomPlayer("R1"));
        players.add(new NewRandomPlayer("R2"));
        players.add(new NewRandomPlayer("R3"));
        long timeLimit = 300000000000l;
        long seed = 27081883;
        int bestOf = 1;
        NewMatch.MatchType matchType = NewMatch.MatchType.CROSSING_ANGLE;
        PlanarGameInstanceFactory factory = new PlanarGameInstanceFactory(seed);
        runLeague(players,bestOf,timeLimit,factory,matchType,seed);
        //runRemainingMatches(player,players,bestOf,timeLimit,factory);
    }

    public static void runLeague(ArrayList<NewPlayer> players, int bestOf, long timeLimit, GameInstanceFactory factory, NewMatch.MatchType matchType, long seed)
    {
        NewLeague l = new NewLeague(players,bestOf,timeLimit,factory,matchType,seed);
        NewLeagueResult lr = l.runLeague();
        System.out.println(lr.announceResults());
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
