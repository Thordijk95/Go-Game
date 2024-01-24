package GameTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import Connectivity.Server.ConnectionHandler;
import Game.Board;
import Game.GoGame;
import Game.Game;
import Game.*;
import com.nedap.go.Go;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Logic_Test {
  public static final int dimension = 9;
  public Game goGame;
  public Board board;
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
    Position newPosition = new Position(dimension);
    // create a line of black on index 0
    for (int i = 0; i < dimension; i++) {
      newPosition.setIntersection(i, Stone.BLACK);
      newPosition.setIntersection(i+dimension, Stone.WHITE);
    }

    newPosition.setIntersection(27, Stone.WHITE);
    newPosition.setIntersection(37, Stone.WHITE);

    newPosition.setIntersection(40, Stone.BLACK);
    newPosition.setIntersection(41, Stone.BLACK);
    newPosition.setIntersection(48, Stone.BLACK);
    newPosition.setIntersection(49, Stone.BLACK);
    newPosition.setIntersection(50, Stone.BLACK);
    newPosition.setIntersection(59, Stone.BLACK);
    newPosition.setIntersection(58, Stone.BLACK);


    goGame.setPosition(newPosition);
    System.out.println(goGame.getStateString());
    List<Cluster> clusters = logic.stoneClusters(goGame.getStatePosition(), dimension);
    assertEquals(4,clusters.size());
    // Check that the borders of each cluster are found correctly
    assertEquals(clusters.get(0).intersectionList.size(), clusters.get(0).coordinatesBorder.size());
    assertEquals(clusters.get(1).intersectionList.size(), clusters.get(1).coordinatesBorder.size());
    assertEquals(clusters.get(2).intersectionList.size(), clusters.get(2).coordinatesBorder.size());
    // Test cluster with 1 stone not part of the boarder
    assertEquals(clusters.get(3).intersectionList.size(), clusters.get(3).coordinatesBorder.size()+1);
  }

  @Test
  void testScoring() {
    Position newPosition = new Position(dimension);

  }
}
