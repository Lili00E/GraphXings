package GraphXings.Competitors.Group08;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Data.Coordinate;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * A player performing random moves.
 */
public class EfficientWinningPlayer implements NewPlayer
{
    	/**
	 * The name of the random player.
	 */
	private final String name;
	/**
	 * A random number generator.
	 */
	private final Random r;
	/**
	 * The graph to be drawn.
	 */
	private Graph g;
	/**
	 * The current state of the game;
	 */
	private GameState gs;
	/**
	 * The width of the game board.
	 */
	private int width;
	/**
	 * The height of the game board.
	 */
	private int height;

    /**
     * Creates a random player with the assigned name.
     * @param name
     */
    public EfficientWinningPlayer(String name)
    {
        this.name = name;
        this.r =  new Random(name.hashCode());
    }

    @Override
    public GameMove maximizeCrossings(GameMove lastMove) {
        if (lastMove != null)
		{
			gs.applyMove(lastMove);
		}
        GameMove move = max(lastMove);
        gs.applyMove(move);
        return move;
    }
    
    @Override
    public GameMove minimizeCrossings(GameMove lastMove) {
        if (lastMove != null)
		{
			gs.applyMove(lastMove);
		}
        
        GameMove move = min(lastMove);
        gs.applyMove(move);
        return move;
    }

    @Override
    public void initializeNextRound(Graph g, int width, int height, Role role)
    {
        this.g = g;
		this.width = width;
		this.height = height;
		this.gs = new GameState(width,height);
    }

    /**
     * Computes a random valid move.
     * @param g The graph.
     * @param usedCoordinates The used coordinates.
     * @param placedVertices The already placed vertices.
     * @param width The width of the game board.
     * @param height The height of the game board.
     * @return A random valid move.
     */
    private GameMove randomMove()
	{
		int stillToBePlaced = g.getN()- gs.getPlacedVertices().size();
		int next = r.nextInt(stillToBePlaced);
		int skipped = 0;
		Vertex v=null;
		for (Vertex u : g.getVertices())
		{
			if (!gs.getPlacedVertices().contains(u))
			{
				if (skipped < next)
				{
					skipped++;
					continue;
				}
				v=u;
				break;
			}
		}
		Coordinate c;
		do
		{
			c = new Coordinate(r.nextInt(width),r.nextInt(height));
		}
		while (gs.getUsedCoordinates()[c.getX()][c.getY()]!=0);
		return new GameMove(v,c);
	}

    private GameMove min(GameMove lastMove) {

        try {
            Vertex lastElement = lastMove.getVertex();

            Vertex newVertex = createVertex(lastElement, gs.getPlacedVertices());
            boolean exists = checkIfExists(gs.getPlacedVertices(), newVertex);
            // Coordinate newCoordinate = new Coordinate(0, 0);
            if (!exists) {
                Coordinate c = lastMove.getCoordinate();
                // Coordinate[] possibleCoordinates = getSurroundingCoordinates(c);
                // newCoordinate = new Coordinate(c.getX() + 1, c.getY() + 1);
                
                getPositions pos = new getPositions();
                List<Coordinate> coords = getPositions.generateNeighbors(c, 8);
                for (Coordinate cc : coords) {
                    GameMove newMove =  new GameMove(newVertex, cc);
                    if (gs.checkMoveValidity(newMove)) {
                        // System.out.println("here min");
                        return newMove;
                    }
                }

                return minBrutGameMove();
                
            }
        } catch (Exception e){
            // System.err.println("error min");
            return minBrutGameMove();
        }


        // System.out.println("not here min");
        return minBrutGameMove();

    }

    private GameMove max(GameMove lastMove) {

        try {
            Vertex lastElement = lastMove.getVertex();

            Vertex newVertex = createVertex(lastElement, gs.getPlacedVertices());
            boolean exists = checkIfExists(gs.getPlacedVertices(), newVertex);
            Coordinate c = lastMove.getCoordinate();
            Coordinate miirorCoordinate = mirrorPoint(c, gs.getUsedCoordinates());
            if (!exists) {                

                GameMove newMove =  new GameMove(newVertex, miirorCoordinate);
                if (gs.checkMoveValidity(newMove)) {
                    // System.out.println("here max");
                    return newMove;
                }
                
                // System.out.println("max max max");
                return maxBrutGameMove();
                
            }
            
        } catch (Exception e){
            // System.err.println("exception max");
            return maxBrutGameMove();
        }


        // System.out.println("not here max");
        return maxBrutGameMove();

    }

    // private Coordinate getCoordinateOfVertex(HashMap<Vertex, Coordinate> vertexCoordinates, Vertex v) {
    //     for (Map.Entry<Vertex, Coordinate> entry : vertexCoordinates.entrySet()) {
    //         Vertex vertex = entry.getKey();
    //         Coordinate coordinate = entry.getValue();
    //         if (vertex.getId() == v.getId()) {
    //             return coordinate;
    //         }
    //     }

    //     return new Coordinate(0,0);
    // }

    private Vertex createVertex(Vertex v, HashSet<Vertex> placedVertices) {
        

        for (int i = 1; i < 40; i++) {
            int id = Integer.valueOf(v.getId());
            int newId = id + i;
            String newIdd = String.valueOf(newId);
            Vertex temp = new Vertex(newIdd);
            if (!checkIfExists(placedVertices, temp)) {
                return temp;
            }

            int newIdX = id + (-i);
            String newIddX = String.valueOf(newIdX);
            Vertex tempX = new Vertex(newIddX);

            if(!checkIfExists(placedVertices, tempX)) {
                return tempX;
            }

        }
        return new Vertex(v.getId() + 1);
    }

    private boolean checkIfExists(HashSet<Vertex> placedVertices, Vertex v) {
        for (Vertex vv : placedVertices) {
            if (vv.getId() == v.getId()) {
                return true;
            }
        }

        return false;
    }

    static Coordinate mirrorPoint(Coordinate c, int[][] field) {
        int xLen = field[0].length / 2;
        int yLen = field.length / 2;
    
        int mirroredX = xLen + (xLen - c.getX());
        int mirroredY = yLen + (yLen - c.getY());
    
        return new Coordinate(mirroredX, mirroredY);
    }

    private GameMove maxBrutGameMove() {
        SweepLineIntersection sweep = new SweepLineIntersection();
        int max = Integer.MIN_VALUE;
        GameMove randGameMove = new GameMove(null, null);
        GameMove bestMove = randomMove();
        for(int i = 0; i < 25; i++) {
            randGameMove = randomMove();
            HashMap<Vertex,Coordinate> vertexCoordinates = gs.getVertexCoordinates();

            int number = sweep.computeCrossingNumber(g, vertexCoordinates);

            if (number > max) {
                max = number;
                bestMove = randGameMove;
            }

        }

        return bestMove;
    }

    private GameMove minBrutGameMove() {
        SweepLineIntersection sweep = new SweepLineIntersection();
        int min = Integer.MAX_VALUE;
        GameMove randGameMove = new GameMove(null, null);
        GameMove bestMove = randomMove();
        for(int i = 0; i < 25; i++) {
            randGameMove = randomMove();
            HashMap<Vertex,Coordinate> vertexCoordinates = gs.getVertexCoordinates();

            int number = sweep.computeCrossingNumber(g, vertexCoordinates);

            if (number < min) {
                min = number;
                bestMove = randGameMove;
            }

        }

        return bestMove;
    }

    @Override
    public String getName()
    {
        return name;
    }
    
}