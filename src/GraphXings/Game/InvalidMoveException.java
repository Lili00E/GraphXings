package GraphXings.Game;

import GraphXings.Algorithms.Player;

public class InvalidMoveException extends Exception {
    private Player cheater;

    public InvalidMoveException(Player cheater) {
        this.cheater = cheater;
    }

    public Player getCheater() {
        return cheater;
    }
}
