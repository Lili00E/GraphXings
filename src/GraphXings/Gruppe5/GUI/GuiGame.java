
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
    public double playRound(NewPlayer maximizer, NewPlayer minimizer)
            throws NewInvalidMoveException, NewTimeOutException {
        int turn = 0;
        GameState gs = new GameState(g, width, height);
        GameMove lastMove = null;
        long timeMaximizer = 0;
        long timeMinimizer = 0;
        long initStartTimeMax = System.nanoTime();
        NewPlayer.Role maximizerRole;
        NewPlayer.Role minimizerRole;
        if (objective.equals(Objective.CROSSING_NUMBER)) {
            maximizerRole = NewPlayer.Role.MAX;
            minimizerRole = NewPlayer.Role.MIN;
        } else {
            maximizerRole = NewPlayer.Role.MAX_ANGLE;
            minimizerRole = NewPlayer.Role.MIN_ANGLE;
        }
        maximizer.initializeNextRound(g.copy(), width, height, maximizerRole);
        timeMaximizer += System.nanoTime() - initStartTimeMax;
        if (timeMaximizer > timeLimit) {
            throw new NewTimeOutException(maximizer);
        }
        long initStartTimeMin = System.nanoTime();
        minimizer.initializeNextRound(g.copy(), width, height, minimizerRole);
        timeMinimizer += System.nanoTime() - initStartTimeMin;
        if (timeMinimizer > timeLimit) {
            throw new NewTimeOutException(minimizer);
        }
        System.out.println("Minimizer is: " + minimizer.getName() + " RED");
        System.out.println("Maximizer is: " + maximizer.getName() + " BLUE");

        while (turn < g.getN()) {
            GameMove newMove;
            if (turn % 2 == 0) {
                long moveStartTime = System.nanoTime();
                try {
                    if (objective.equals(Objective.CROSSING_NUMBER)) {
                        newMove = maximizer.maximizeCrossings(lastMove);
                    } else {
                        newMove = maximizer.maximizeCrossingAngles(lastMove);
                    }
                } catch (Exception ex) {
                    System.out.println("E003:" + maximizer.getName() + " threw a " + ex.getClass() + " exception!");
                    System.out.println(ex.getMessage());
                    // print stack trace
                    System.out.println("Stack trace:");
                    ex.printStackTrace();
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
                    if (objective.equals(Objective.CROSSING_NUMBER)) {
                        newMove = minimizer.minimizeCrossings(lastMove);
                    } else {
                        newMove = minimizer.minimizeCrossingAngles(lastMove);
                    }
                } catch (Exception ex) {
                    System.out.println("E004:" + minimizer.getName() + " threw a " + ex.getClass() + " exception!");
                    // print stack printStackTrace
                    System.out.println("Stack trace:");
                    ex.printStackTrace();
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
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            this.display.updateGameState(gs, turn, newMove, g);
            turn++;
        }
        this.display.repaint();
        CrossingCalculator cc = new CrossingCalculator(g, gs.getVertexCoordinates());
        if (objective.equals(Objective.CROSSING_NUMBER)) {
            return cc.computeCrossingNumber();
        } else {
            return cc.computeSumOfSquaredCosinesOfCrossingAngles();
        }
    }

}

// @Override
// protected int playRound(NewPlayer maximizer, NewPlayer minimizer)
// throws NewInvalidMoveException, NewTimeOutException {

// GameState gs = new GameState(g, width, height);
// GameMove lastMove = null;
// long timeMaximizer = 0;
// long timeMinimizer = 0;
// turn = 0;
// while (turn < g.getN()) {
// GameMove newMove;
// if (turn % 2 == 0) {
// long moveStartTime = System.nanoTime();
// try {
// newMove = maximizer.maximizeCrossings(lastMove);
// } catch (Exception ex) {
// System.out.println("E003:" + maximizer.getName() + " threw a " +
// ex.getClass() + " exception!");
// throw new NewInvalidMoveException(maximizer);
// }
// timeMaximizer += System.nanoTime() - moveStartTime;
// if (timeMaximizer > timeLimit) {
// throw new NewTimeOutException(maximizer);
// }
// if (!gs.checkMoveValidity(newMove)) {
// throw new NewInvalidMoveException(maximizer);
// }
// } else {
// long moveStartTime = System.nanoTime();
// try {
// newMove = minimizer.minimizeCrossings(lastMove);
// } catch (Exception ex) {
// System.out.println("E004:" + minimizer.getName() + " threw a " +
// ex.getClass() + " exception!");
// throw new NewInvalidMoveException(minimizer);
// }
// timeMinimizer += System.nanoTime() - moveStartTime;
// if (timeMinimizer > timeLimit) {
// throw new NewTimeOutException(minimizer);
// }
// if (!gs.checkMoveValidity(newMove)) {
// throw new NewInvalidMoveException(minimizer);
// }
// }
// gs.applyMove(newMove);
// lastMove = newMove;
// turn++;
// this.display.updateGameState(gs, turn, newMove, g);
// try {
// Thread.sleep(300);

// } catch (InterruptedException exc) {
// System.out.println("Sleep interrupted");

// }
// }
// CrossingCalculator cc = new CrossingCalculator(g, gs.getVertexCoordinates());
// return cc.computeCrossingNumber();
// }
// }
