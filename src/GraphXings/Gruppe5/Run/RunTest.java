package GraphXings.Gruppe5.Run;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Game.GameInstance.GameInstanceFactory;
import GraphXings.Game.GameInstance.PlanarGameInstanceFactory;
import GraphXings.Game.GameInstance.RandomCycleFactory;
import GraphXings.Game.League.NewLeague;
import GraphXings.Game.League.NewLeagueResult;
import GraphXings.Game.Match.NewMatch;
import GraphXings.GraphXings;
import GraphXings.Gruppe8.EfficientWinningPlayer;
import GraphXings.Gruppe5.Models.HeatMapFileReader;
import GraphXings.Gruppe5.Players.PointChoicePlayer;
import GraphXings.Gruppe5.PointStrategies.HeatMapChoiceStrategy;
import GraphXings.Gruppe5.Utils.CSVWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.HashMap;
import java.util.logging.FileHandler;

import static GraphXings.GraphXings.runLeague;

public class RunTest {
  public static void main(String[] args) throws IOException {
    long timeLimit = 300000000000l;
    long seed = 1;
    int bestOf = 1;
    java.util.logging.Logger logger = java.util.logging.Logger.getLogger("TestRun");
    FileHandler fileHandler = new FileHandler("status.log");
    logger.addHandler(fileHandler);

    // logger.info("This is an info message");
    // logger.severe("This is an error message"); // == ERROR
    // logger.fine("Here is a debug message"); // == DEBUG
    var csvWriter = new CSVWriter(new ArrayList<String>() {
      {
        add("our_player_name");
        add("other_player_name");
        add("graph_width");
        add("graph_height");
        add("num_nodes");
        add("factory");
        add("match_type");
        add("final_score"); // ex: "2:1" my player first
      }
    });

    // Heatmaps
    var smallHeatMapMin = new HeatMapFileReader()
        .readFromFile("./src/GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_2.txt");
    var smallHeatMapMax = new HeatMapFileReader()
        .readFromFile("./src/GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_3.txt");

    // our Players
    NewPlayer smallHeatmapPlayer = new PointChoicePlayer("SmallHeatmap", new HeatMapChoiceStrategy(smallHeatMapMin),
        new HeatMapChoiceStrategy(smallHeatMapMax));
    ArrayList<NewPlayer> ourPlayers = new ArrayList<>();
    ourPlayers.add(smallHeatmapPlayer);

    // other Players
    ArrayList<NewPlayer> gameGruppe8 = new ArrayList<>();
    gameGruppe8.add(0, new EfficientWinningPlayer("Gruppe 8"));

    // ArrayList<NewPlayer> gameGruppe10 = new ArrayList<>();
    // gameGruppe10.add(0, new LighthousePlayer());

    ArrayList<ArrayList<NewPlayer>> competitorGroups = new ArrayList<>();
    competitorGroups.add(gameGruppe8);
    // listOfCompetitors.add(gameGruppe10);

    // Match Types
    ArrayList<NewMatch.MatchType> matchTypes = new ArrayList<>();

    NewMatch.MatchType angles = NewMatch.MatchType.CROSSING_ANGLE;
    NewMatch.MatchType crossings = NewMatch.MatchType.CROSSING_NUMBER;

    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
    df.setTimeZone(tz);
    String nowAsISO = df.format(new Date());
    matchTypes.add(angles);
    // matchTypes.add(crossings);

    // Factories
    HashMap<String, GameInstanceFactory> factories = new HashMap<>();

    PlanarGameInstanceFactory planarTriangulation = new PlanarGameInstanceFactory(seed);
    RandomCycleFactory cycleNoMatching = new RandomCycleFactory(seed, false);
    RandomCycleFactory cycleMatching = new RandomCycleFactory(seed, true);

    factories.put("planarTriangulation", planarTriangulation);
    // factories.put("cycleMatching", cycleMatching);
    // factories.put("cycleNoMatching", cycleNoMatching);
    for (ArrayList<NewPlayer> competitorGroup : competitorGroups) {
      for (NewPlayer competitorPlayer : competitorGroup) {
        System.out.println("Competitor: " + competitorPlayer.getName());

        for (NewPlayer ourPlayer : ourPlayers) {
          // TODO: implement getHeatmapName method in Pointchoiceplayer
          for (String factory : factories.keySet()) {
            System.out.println("Current Factory: " + factory);
            var game = factories.get(factory).getGameInstance();

            for (NewMatch.MatchType matchType : matchTypes) {
              System.out.println("MatchType: " + matchType.name());
              System.out.println(" ");
              System.out.println(game.getG().getN() + " nodes");
              var players = new ArrayList<NewPlayer>();
              players.add(ourPlayer);
              players.add(competitorPlayer);
              var factoryInstance = factories.get(factory);
              NewLeague l = new NewLeague(players, bestOf, timeLimit, factoryInstance, matchType, seed);
              NewLeagueResult lr = l.runLeague();
              String[] row = {
                  ourPlayer.getName(),
                  competitorPlayer.getName(),
                  String.valueOf(game.getWidth()),
                  String.valueOf(game.getHeight()),
                  String.valueOf(game.getG().getN()),
                  factory,
                  matchType.name(),
                  lr.getScore(ourPlayer) + ":" + lr.getScore(competitorPlayer),
              };
              csvWriter.write(row);
            }
          }
        }
      }
    }
    csvWriter.writeToFile("./out_" + nowAsISO + ".csv");
  }

}
