package GameTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import Connectivity.Server.ConnectionHandler;
import Game.GoGame;
import Game.Game;
import Game.*;
import com.nedap.go.Go;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Logic_Test {

  public int dimension;
  public Game goGame;
  public GoLogic logic;
  public List<ConnectionHandler> players = new ArrayList<>();
  public ConnectionHandler player1 = new ConnectionHandler();
  public ConnectionHandler player2 = new ConnectionHandler();

  /**
   * Before every test create a board and add two players to it.
   */
  @BeforeEach
  void setup() {
    logic = new GoLogic();
    players.add(player1);
    players.add(player2);
    goGame = new GoGame(dimension, players);
  }

  @Test
  void testClusters() {
    dimension = 9;
    logic.dimension = dimension;
    Position newPosition = new Position(dimension);
    // create a line of black on index 0
    for (int i = 0; i < dimension; i++) {
      newPosition.setIntersection(i, Stone.BLACK);
      newPosition.setIntersection(i + dimension, Stone.WHITE);
    }

    newPosition.setIntersection(27, Stone.WHITE);
    newPosition.setIntersection(37, Stone.WHITE);

    newPosition.setIntersection(40, Stone.BLACK);
    newPosition.setIntersection(41, Stone.BLACK);
    newPosition.setIntersection(48, Stone.BLACK);
    newPosition.setIntersection(50, Stone.BLACK);
    newPosition.setIntersection(59, Stone.BLACK);
    newPosition.setIntersection(58, Stone.BLACK);

    goGame.setPosition(newPosition);
    System.out.println(goGame.getStateString());
    HashMap<Stone, List<Cluster>> clustersHashMap = logic.stoneClusters(goGame.getStatePosition());

    List<Cluster> blackClusters = clustersHashMap.get(Stone.BLACK);
    List<Cluster> whiteClusters = clustersHashMap.get(Stone.WHITE);

    // Check that all clusters are found, and have the correct size
    // check that the borders of each cluster are found correctly
    assertEquals(2, blackClusters.size());
    assertEquals(2, whiteClusters.size());

    assertEquals(dimension, blackClusters.getFirst().coordinatesBorder.size());
    assertEquals(dimension, blackClusters.getFirst().coordinatesBorder.size());

    assertEquals(dimension, whiteClusters.getFirst().coordinatesBorder.size());
    assertEquals(dimension, whiteClusters.getFirst().coordinatesBorder.size());

    assertEquals(2, whiteClusters.get(1).intersectionList.size());
    assertEquals(2, whiteClusters.get(1).coordinatesBorder.size());
    // Test cluster with 1 stone not part of the boarder
    assertEquals(6, blackClusters.get(1).intersectionList.size());
    assertEquals(6, blackClusters.get(1).coordinatesBorder.size());
  }

  @Test
  void testScoring() {
    Position newPosition = new Position(dimension);

  }
}
