package GraphXings.GUI;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

import GraphXings.Data.Vertex;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameResult;
import GraphXings.Game.InvalidMoveException;
import bentley_ottmann.Segment;
import bentley_ottmann.Point;
import GraphXings.Algorithms.BasicCrossingCalculatorAlgorithm;
import GraphXings.Algorithms.BentleyOttmannCrossingCalculator;
import GraphXings.Algorithms.CrossingCalculator;
import GraphXings.Algorithms.Player;
import GraphXings.Algorithms.RationalComputer;
import GraphXings.Algorithms.Utils;
import GraphXings.Data.Coordinate;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;

/**
 * Created by valen_000 on 15. 5. 2017.
 */

public class GUIGame extends JFrame {

    /**
     * The width of the game board.
     */
    private int width;
    /**
     * The height of the game board.
     */
    private int height;
    /**
     * The graph to be drawn.
     */
    private Graph graph;
    /**
     * The first player.
     */
    private Player player1;
    /**
     * The second player.
     */
    private Player player2;

    int turn = 0;
    int crossingNumber = 0;

    LinkedList<GameMove> gameMoves = new LinkedList<>();
    public HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>();
    HashSet<Vertex> placedVertices = new HashSet<>();
    int[][] usedCoordinates;

    /**
     * Instantiates a game of GraphXings.
     * 
     * @param g       The graph to be drawn.
     * @param width   The width of the game board.
     * @param height  The height of the game board.
     * @param player1 The first player. Plays as the maximizer in round one.
     * @param player2 The second player. Plays as the minimizer in round one.
     */
    public GUIGame(Graph g, int width, int height, Player player1, Player player2) {
        this.graph = g;
        this.width = width;
        this.height = height;
        this.player1 = player1;
        this.player2 = player2;
        usedCoordinates = new int[width][height];

        JPanel panel = new JPanel();
        getContentPane().add(panel);

        setSize(width, height);
        setTitle("Bentley-Ottmann algorithm");

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getRootPane().registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        getRootPane().registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    playSingleTurn(player1, player2);

                    repaint();

                } catch (InvalidMoveException ex) {
                    System.err.println("Found invalid move!");
                }
            }

        }, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        setVisible(true);
    }

    public void initGameRound() {
        gameMoves = new LinkedList<>();
        vertexCoordinates = new HashMap<>();
        placedVertices = new HashSet<>();
        usedCoordinates = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                usedCoordinates[x][y] = 0;
            }
        }
    }

    private ArrayList<Segment> getSegments() {
        var segments = new ArrayList<Segment>();
        for (var e : graph.getEdges()) {

            var startCoord = vertexCoordinates.get(e.getS());
            var endCoord = vertexCoordinates.get(e.getT());

            if (startCoord == null || endCoord == null) {
                continue;
            }

            segments.add(new Segment(new Point(startCoord.getX(), startCoord.getY()),
                    new Point(endCoord.getX(), endCoord.getY())));
        }
        return segments;
    }

    public void playSingleTurn(Player maximizer, Player minimizer) throws InvalidMoveException {

        if (turn < graph.getN()) {

            GameMove newMove;
            Graph copyOfG = graph.copy();
            LinkedList<GameMove> copyOfGameMoves = copyGameMoves(gameMoves);
            HashMap<Vertex, Coordinate> copyOfVertexCoordinates = copyVertexCoordinates(vertexCoordinates);
            HashSet<Vertex> copyOfPlacedVertices = copyPlacedVertices(placedVertices);
            int[][] copyOfUsedCoordinates = copyUsedCoordinates(usedCoordinates);
            if (turn % 2 == 0) {
                newMove = maximizer.maximizeCrossings(copyOfG, copyOfVertexCoordinates, copyOfGameMoves,
                        copyOfUsedCoordinates, copyOfPlacedVertices, width, height);
                if (!checkMoveValidity(newMove, placedVertices, usedCoordinates)) {
                    throw new InvalidMoveException(maximizer);
                }
            } else {
                newMove = minimizer.minimizeCrossings(copyOfG, copyOfVertexCoordinates, copyOfGameMoves,
                        copyOfUsedCoordinates, copyOfPlacedVertices, width, height);
                if (!checkMoveValidity(newMove, placedVertices, usedCoordinates)) {
                    throw new InvalidMoveException(minimizer);
                }
            }
            gameMoves.add(newMove);
            usedCoordinates[newMove.getCoordinate().getX()][newMove.getCoordinate().getY()] = 1;
            placedVertices.add(newMove.getVertex());
            vertexCoordinates.put(newMove.getVertex(), newMove.getCoordinate());
            turn++;

        } else {
            CrossingCalculator cc = new CrossingCalculator(graph, vertexCoordinates);
            try {
                int crossingNumbers = cc.computeCrossingNumber();
                System.out.println("Found " + crossingNumbers + " crossings!");

            } catch (ArithmeticException e) {
                throw e;
            }
        }

    }

    /**
     * Runs the full game of GraphXings.
     * 
     * @return Provides a GameResult Object containing the game's results.
     */
    public GameResult playFullGame() {
        try {
            long timeStart = System.currentTimeMillis();
            int crossingsGame1 = playRound(player1, player2);
            long timeEnd = System.currentTimeMillis();
            Utils.announceTimedFunction("Playing Initial Round", timeStart, timeEnd);
            timeStart = System.currentTimeMillis();
            player1.initializeNextRound();
            player2.initializeNextRound();
            timeEnd = System.currentTimeMillis();
            Utils.announceTimedFunction("Init Round", timeStart, timeEnd);
            timeStart = System.currentTimeMillis();
            int crossingsGame2 = playRound(player2, player1);
            timeEnd = System.currentTimeMillis();
            Utils.announceTimedFunction("Playing Second Round", timeStart, timeEnd);
            return new GameResult(crossingsGame1, crossingsGame2, player1, player2, false, false);
        } catch (InvalidMoveException ex) {
            if (ex.getCheater().equals(player1)) {
                return new GameResult(0, 0, player1, player2, true, false);
            } else if (ex.getCheater().equals(player2)) {
                return new GameResult(0, 0, player1, player2, false, true);
            } else {
                return new GameResult(0, 0, player1, player2, false, false);
            }
        }
    }

    /**
     * Plays a single round of the game.
     * 
     * @param maximizer The player with the goal to maximize the number of
     *                  crossings.
     * @param minimizer The player with the goal to minimize the number of crossings
     * @return The number of crossings yielded in the final drawing.
     * @throws InvalidMoveException An exception caused by cheating.
     */
    private int playRound(Player maximizer, Player minimizer) throws InvalidMoveException {
        int turn = 0;
        LinkedList<GameMove> gameMoves = new LinkedList<>();
        HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>();
        HashSet<Vertex> placedVertices = new HashSet<>();
        int[][] usedCoordinates = new int[width][height];

        long timeStart = System.currentTimeMillis();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                usedCoordinates[x][y] = 0;
            }
        }
        long timeEnd = System.currentTimeMillis();
        System.out.println("initializeCoordinates(): " + (timeEnd - timeStart) + " Millisek.");

        while (turn < graph.getN()) {
            timeStart = System.currentTimeMillis();
            GameMove newMove;
            Graph copyOfG = graph.copy();
            LinkedList<GameMove> copyOfGameMoves = copyGameMoves(gameMoves);
            HashMap<Vertex, Coordinate> copyOfVertexCoordinates = copyVertexCoordinates(vertexCoordinates);
            HashSet<Vertex> copyOfPlacedVertices = copyPlacedVertices(placedVertices);
            int[][] copyOfUsedCoordinates = copyUsedCoordinates(usedCoordinates);
            if (turn % 2 == 0) {
                newMove = maximizer.maximizeCrossings(copyOfG, copyOfVertexCoordinates, copyOfGameMoves,
                        copyOfUsedCoordinates, copyOfPlacedVertices, width, height);
                if (!checkMoveValidity(newMove, placedVertices, usedCoordinates)) {
                    throw new InvalidMoveException(maximizer);
                }
            } else {
                newMove = minimizer.minimizeCrossings(copyOfG, copyOfVertexCoordinates, copyOfGameMoves,
                        copyOfUsedCoordinates, copyOfPlacedVertices, width, height);
                if (!checkMoveValidity(newMove, placedVertices, usedCoordinates)) {
                    throw new InvalidMoveException(minimizer);
                }
            }
            gameMoves.add(newMove);
            usedCoordinates[newMove.getCoordinate().getX()][newMove.getCoordinate().getY()] = 1;
            placedVertices.add(newMove.getVertex());
            vertexCoordinates.put(newMove.getVertex(), newMove.getCoordinate());
            turn++;
            timeEnd = System.currentTimeMillis();

        }
        CrossingCalculator cc = new CrossingCalculator(graph, vertexCoordinates);
        try {
            int crossingNumbers = cc.computeCrossingNumber();
            return crossingNumbers;

        } catch (ArithmeticException e) {
            throw e;
        }

    }

    /**
     * Checks if a move is valid given the current state of the game.
     * 
     * @param newMove         The potential move to be performed.
     * @param placedVertices  The vertices that already are placed.
     * @param usedCoordinates A 0-1-map of the coordinates. 1 indicates an already
     *                        used coordinate.
     * @return True if the move is valid, false if it is invalid.
     */
    private boolean checkMoveValidity(GameMove newMove, HashSet<Vertex> placedVertices, int[][] usedCoordinates) {
        if (newMove.getVertex() == null || newMove.getCoordinate() == null) {
            return false;
        }
        if (placedVertices.contains(newMove.getVertex())) {
            return false;
        }
        int x = newMove.getCoordinate().getX();
        int y = newMove.getCoordinate().getY();
        if (x >= width || y >= height) {
            return false;
        }
        if (usedCoordinates[x][y] != 0) {
            return false;
        }
        return true;
    }

    /**
     * Creates a copy of the vertex coordinates.
     * 
     * @param vertexCoordinates The original vertex coordinates.
     * @return A copy of vertexCoordinates.
     */
    private HashMap<Vertex, Coordinate> copyVertexCoordinates(HashMap<Vertex, Coordinate> vertexCoordinates) {
        HashMap<Vertex, Coordinate> copy = new HashMap<>();
        for (Vertex v : vertexCoordinates.keySet()) {
            copy.put(v, vertexCoordinates.get(v));
        }
        return copy;
    }

    /**
     * Creates a copy of the list of placed vertices.
     * 
     * @param placedVertices The original list of placed vertices.
     * @return A copy of placedVertices.
     */
    private HashSet<Vertex> copyPlacedVertices(HashSet<Vertex> placedVertices) {
        HashSet<Vertex> copy = new HashSet<>();
        for (Vertex v : placedVertices) {
            copy.add(v);
        }
        return copy;
    }

    /**
     * Returns a copy of the list of prior game moves.
     * 
     * @param gameMoves The list of prior game moves.
     * @return A copy of gameMoves.
     */
    private LinkedList<GameMove> copyGameMoves(LinkedList<GameMove> gameMoves) {
        LinkedList<GameMove> copy = new LinkedList<>();
        for (int i = 0; i < gameMoves.size(); i++) {
            copy.add(gameMoves.get(i));
        }
        return copy;
    }

    /**
     * Returns a copy of the map of used coordinates.
     * 
     * @param usedCoordinates The original map of used coordinates.
     * @return A copy of usedCoordinates.
     */
    private int[][] copyUsedCoordinates(int[][] usedCoordinates) {
        int[][] copy = new int[usedCoordinates.length][usedCoordinates[0].length];
        for (int i = 0; i < usedCoordinates.length; i++) {
            for (int j = 0; j < usedCoordinates[0].length; j++) {
                copy[i][j] = usedCoordinates[i][j];
            }
        }
        return copy;
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);
        g.clearRect(0, 0, width, height);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        for (Segment s : this.getSegments()) {
            Line2D.Double segment = new Line2D.Double(s.first().getXCoord(), s.first().getYCoord(),
                    s.second().getXCoord(), s.second().getYCoord());
            g2.draw(segment);
        }

        for (var coords : vertexCoordinates.values()) {

            double vertexSize = 20.0;

            double new_x = coords.getX() - vertexSize / 2.0;
            double new_y = coords.getY() - vertexSize / 2.0;
            Ellipse2D.Double point = new Ellipse2D.Double(new_x, new_y, vertexSize, vertexSize);
            g2.setPaint(Color.RED);
            // g2.fill(point);
            g2.draw(point);
        }

        var cc = new BasicCrossingCalculatorAlgorithm();
        cc.computeCrossingNumber(graph, vertexCoordinates);

        for (var coords : cc.intersectionPoints) {

            double vertexSize = 10.0;

            double new_x = coords.getXCoord() - vertexSize / 2.0;
            double new_y = coords.getYCoord() - vertexSize / 2.0;
            Ellipse2D.Double point = new Ellipse2D.Double(new_x, new_y, vertexSize, vertexSize);
            g2.setPaint(Color.GREEN);
            g2.fill(point);
            g2.draw(point);
        }

    }
}