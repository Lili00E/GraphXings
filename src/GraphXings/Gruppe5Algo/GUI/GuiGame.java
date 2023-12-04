package GraphXings.Gruppe5Algo.GUI;

import GraphXings.Algorithms.CrossingCalculator;
import GraphXings.Algorithms.NewPlayer;
import GraphXings.Data.Graph;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameState;
import GraphXings.Game.NewGame;
import GraphXings.Game.NewInvalidMoveException;
import GraphXings.Game.NewTimeOutException;

public class GuiGame extends NewGame {

    int turn = 0;
    GuiGameDisplay display;

    public GuiGame(Graph g, int width, int height, NewPlayer player1, NewPlayer player2) {
        super(g, width, height, player1, player2);
        this.display = new GuiGameDisplay(width, height);
    }

    @Override
    protected int playRound(NewPlayer maximizer, NewPlayer minimizer)
            throws NewInvalidMoveException, NewTimeOutException {

        GameState gs = new GameState(g, width, height);
        GameMove lastMove = null;
        long timeMaximizer = 0;
        long timeMinimizer = 0;
        turn = 0;
        while (turn < g.getN()) {
            GameMove newMove;
            if (turn % 2 == 0) {
                long moveStartTime = System.nanoTime();
                try {
                    newMove = maximizer.maximizeCrossings(lastMove);
                } catch (Exception ex) {
                    System.out.println("E003:" + maximizer.getName() + " threw a " + ex.getClass() + " exception!");
                    throw new NewInvalidMoveException(maximizer);
                }
                timeMaximizer += System.nanoTime() - moveStartTime;
                if (timeMaximizer > timeLimit) {
                    throw new NewTimeOutException(maximizer);
                }
                if (!gs.checkMoveValidity(newMove)) {
                    throw new NewInvalidMoveException(maximizer);
                }
            } else {
                long moveStartTime = System.nanoTime();
                try {
                    newMove = minimizer.minimizeCrossings(lastMove);
                } catch (Exception ex) {
                    System.out.println("E004:" + minimizer.getName() + " threw a " + ex.getClass() + " exception!");
                    throw new NewInvalidMoveException(minimizer);
                }
                timeMinimizer += System.nanoTime() - moveStartTime;
                if (timeMinimizer > timeLimit) {
                    throw new NewTimeOutException(minimizer);
                }
                if (!gs.checkMoveValidity(newMove)) {
                    throw new NewInvalidMoveException(minimizer);
                }
            }
            gs.applyMove(newMove);
            lastMove = newMove;
            turn++;
            this.display.updateGameState(gs, turn, newMove, g);
            try {
                Thread.sleep(300);

            } catch (InterruptedException exc) {
                System.out.println("Sleep interrupted");

            }
        }
        CrossingCalculator cc = new CrossingCalculator(g, gs.getVertexCoordinates());
        return cc.computeCrossingNumber();
    }
}
