package GraphXings.Gruppe5Algo.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Segment;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameState;
import GraphXings.Gruppe5Algo.Algorithms.BasicCrossingCalculatorAlgorithm;
import GraphXings.Gruppe5Algo.Utils.RationalComputer;

public class GuiGameDisplay extends JFrame {

    protected int width, height;

    protected GameState gs;

    protected HashMap<Vertex, Integer> turnTracker;

    int turn = 0;

    Graph graph;

    public GuiGameDisplay(int width, int height) {

        JPanel panel = new JPanel();
        getContentPane().add(panel);

        this.turnTracker = new HashMap<>();

        setSize(width, height);
        setTitle("GUI Game");

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

        setVisible(true);

    }

    void updateGameState(GameState gs, int turn, GameMove newMove, Graph graph) {
        this.gs = gs;
        this.turn = turn;
        this.turnTracker.put(newMove.getVertex(), turn);
        this.graph = graph;
        repaint();
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);
        g.clearRect(0, 0, width, height);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (this.gs == null) {
            return;
        }

        for (var entry : gs.getVertexCoordinates().entrySet()) {

            double vertexSize = 25.0;

            var coords = entry.getValue();
            var turn = turnTracker.get(entry.getKey());

            double new_x = coords.getX() - vertexSize / 2.0;
            double new_y = coords.getY() - vertexSize / 2.0;
            Ellipse2D.Double point = new Ellipse2D.Double(new_x, new_y, vertexSize, vertexSize);
            if (turn % 2 == 0) // maximizer
                g2.setPaint(Color.RED);
            else
                g2.setPaint(Color.BLUE);

            g2.drawString(turn.toString(), (int) new_x, (int) new_y);
            // g2.fill(point);
            g2.draw(point);
        }

        for (var edge : graph.getEdges()) {

            var startCoord = gs.getVertexCoordinates().get(edge.getS());
            var endCoord = gs.getVertexCoordinates().get(edge.getT());

            if (startCoord == null || endCoord == null) {
                continue;
            }

            var s = new Segment(startCoord,
                    endCoord);

            Line2D.Double segment = new Line2D.Double(RationalComputer.getValue(s.getStartX()),
                    RationalComputer.getValue(s.getStartY()),
                    RationalComputer.getValue(s.getEndX()), RationalComputer.getValue(s.getEndY()));

            g2.setPaint(Color.MAGENTA);
            g2.draw(segment);

        }

    }

}
