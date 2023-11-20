package GraphXings.Gruppe5Algo.Run;

import GraphXings.Algorithms.NewRandomPlayer;
import GraphXings.Game.NewGame;
import GraphXings.Game.NewGameResult;
import GraphXings.Gruppe5Algo.Players.RandomChoicePlayer;
import GraphXings.Gruppe5Algo.Utils.SpecificRandomCycleFactory;
import java.util.HashMap;

public class RunDebug {
    public static void main(String[] args) {
        // Create a graph g. This time it is a 10-cycle!

        int numGames = 50;
        int numNodes = 10;
        int width = 100;
        int height = 100;
        var randomFactory = new SpecificRandomCycleFactory(numNodes, width, height);

        var gameInstance = randomFactory.getGameInstance();

        System.out.println("Generated game with " + gameInstance.getG().getN() + "x" + gameInstance.getWidth() + "x"
                + gameInstance.getHeight());

        var winners = new HashMap<String, Integer>();

        for (int i = 0; i < numGames; i++) {
            NewGame game = new NewGame(gameInstance.getG(), gameInstance.getHeight(), gameInstance.getWidth(),
                    new RandomChoicePlayer("Player 1", 50, 1000), new NewRandomPlayer("Player 2"));
            NewGameResult res = game.play();
            var winnerPlayer = res.getWinner();
            if (winnerPlayer == null) {
                continue;
            }
            var winner = winnerPlayer.getName();
            if (winners.containsKey(winner)) {
                winners.put(winner, winners.get(winner) + 1);
            } else {
                winners.put(winner, 0);
            }

        }

        System.out.println(winners);

    }
}
