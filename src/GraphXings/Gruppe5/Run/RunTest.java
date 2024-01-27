package GraphXings.Gruppe5.Run;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Game.GameInstance.GameInstanceFactory;
import GraphXings.Game.GameInstance.PlanarGameInstanceFactory;
import GraphXings.Game.GameInstance.RandomCycleFactory;
import GraphXings.Game.Match.NewMatch;
import GraphXings.GraphXings;
import GraphXings.Gruppe10.LighthousePlayer;
import GraphXings.Gruppe5.Models.HeatMapFileReader;
import GraphXings.Gruppe5.Players.PointChoicePlayer;
import GraphXings.Gruppe5.PointStrategies.HeatMapChoiceStrategy;
import GraphXings.Gruppe8.EfficientWinningPlayer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.FileHandler;

import static GraphXings.GraphXings.runLeague;

public class RunTest {
    public static void main(String[] args) throws IOException {
        long timeLimit = 300000000000l;
        long seed = 27081884;
        int bestOf = 3;
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger("TestRun");
        FileHandler fileHandler = new FileHandler("status.log");
        logger.addHandler(fileHandler);

//        logger.info("This is an info message");
//        logger.severe("This is an error message"); // == ERROR
//        logger.fine("Here is a debug message"); // == DEBUG

        // Heatmaps
        var smallHeatMapMin = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/SmallHeatMapMin.txt");
        var smallHeatMapMax = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/SmallHeatMapMax.txt");

        // our Players
        NewPlayer smallHeatmapPlayer = new PointChoicePlayer("SmallHeatmap", new HeatMapChoiceStrategy(smallHeatMapMin),
                new HeatMapChoiceStrategy(smallHeatMapMax));
        ArrayList<NewPlayer> ourPlayers = new ArrayList<>();
        ourPlayers.add(smallHeatmapPlayer);


        // other Players
        ArrayList<NewPlayer> gameGruppe8 = new ArrayList<>();
        gameGruppe8.add(0, new EfficientWinningPlayer("Gruppe 8"));

        ArrayList<NewPlayer> gameGruppe10 = new ArrayList<>();
        gameGruppe10.add(0, new LighthousePlayer());

        ArrayList<ArrayList<NewPlayer>> listOfCompetitors = new ArrayList<>();
        listOfCompetitors.add(gameGruppe8);
        listOfCompetitors.add(gameGruppe10);

        // Match Types
        ArrayList<NewMatch.MatchType> matchTypes = new ArrayList<>();

        NewMatch.MatchType angles = NewMatch.MatchType.CROSSING_ANGLE;
        NewMatch.MatchType crossings = NewMatch.MatchType.CROSSING_NUMBER;

        matchTypes.add(angles);
        matchTypes.add(crossings);

        // Factories
        HashMap<String, GameInstanceFactory> factories = new HashMap<>();

        PlanarGameInstanceFactory planarTriangulation = new PlanarGameInstanceFactory(seed);
        RandomCycleFactory cycleNoMatching = new RandomCycleFactory(seed, false);
        RandomCycleFactory cycleMatching = new RandomCycleFactory(seed, true);

        factories.put("planarTriangulation", planarTriangulation);
        factories.put("cycleMatching", cycleMatching);
        factories.put("cycleNoMatching", cycleNoMatching);

        for (ArrayList<NewPlayer> players : listOfCompetitors) {
            System.out.println("###################################################");
            System.out.println("Competitor: " + players.get(0).getName());
            System.out.println("###################################################");
            for(NewPlayer ourPlayer : ourPlayers) {
                players.add(1, ourPlayer);
                System.out.println("Our Heatmap: " + ourPlayer.getName());
                for (String factory : factories.keySet()) {
                    System.out.println("Current Factory: " + factory);

                    System.out.println("Game with " + factories.get(factory).getGameInstance().getG().getN() + "x" + factories.get(factory).getGameInstance().getWidth() + "x"
                            + factories.get(factory).getGameInstance().getHeight());
                    //            logger.info("Generated game with " + factory.getGameInstance().getG().getN() + "x" + factory.getGameInstance().getWidth() + "x"
                    //                    + factory.getGameInstance().getHeight());
                    for (NewMatch.MatchType matchType : matchTypes) {
                        System.out.println("MatchType: " + matchType.name());
                        System.out.println(" ");
                        runLeague(players, bestOf, timeLimit, factories.get(factory), matchType, seed);
                    }
                }
            }
            System.out.println("###################################################");
        }
    }

}
