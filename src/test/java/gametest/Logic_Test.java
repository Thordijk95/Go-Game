package gametest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import connectivity.server.ConnectionHandler;
import game.GoGame;
import game.Game;
import game.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Logic_Test {

  public int dimension;
  public GoGame goGame;
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
  }

  @Test
  void testClusters() {
    goGame = new GoGame(dimension, players);
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
   // HashMap<Stone, List<Cluster>> clustersHashMap = logic.stoneClusters(goGame.getStatePosition());
    HashMap<Stone, List<Cluster>> clusterHashMap2 = logic.findClusters(goGame.getStatePosition());

    List<Cluster> blackClusters = clusterHashMap2.get(Stone.BLACK);
    List<Cluster> whiteClusters = clusterHashMap2.get(Stone.WHITE);
    List<Cluster> noneClusters = clusterHashMap2.get(Stone.NONE);

    // Check that all clusters are found, and have the correct size
    // check that the borders of each cluster are found correctly
    assertEquals(2, blackClusters.size());
    assertEquals(2, whiteClusters.size());
    assertEquals(2, noneClusters.size());

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
    goGame = new GoGame(dimension, players);
    dimension = 9;
    logic.dimension = dimension;
    Position newPosition = new Position(dimension);
    newPosition.setIntersection(0, Stone.BLACK);
    newPosition.score = logic.score(newPosition);
    assertEquals(81, newPosition.score.scoreBlack);

    newPosition.setIntersection(1, Stone.BLACK);
    newPosition.setIntersection(2, Stone.BLACK);
    newPosition.setIntersection(11, Stone.BLACK);
    newPosition.setIntersection(20, Stone.BLACK);
    newPosition.setIntersection(29, Stone.BLACK);
    newPosition.setIntersection(38, Stone.BLACK);
    newPosition.setIntersection(37, Stone.BLACK);
    newPosition.setIntersection(36, Stone.BLACK);

    newPosition.setIntersection(5, Stone.WHITE);
    newPosition.setIntersection(14, Stone.WHITE);
    newPosition.setIntersection(23, Stone.WHITE);
    newPosition.setIntersection(32, Stone.WHITE);
    newPosition.setIntersection(41, Stone.WHITE);
    newPosition.setIntersection(50, Stone.WHITE);
    newPosition.setIntersection(51, Stone.WHITE);
    newPosition.setIntersection(52, Stone.WHITE);
    newPosition.setIntersection(53, Stone.WHITE);

    newPosition.score = logic.score(newPosition);
    System.out.println(newPosition.toString());
    assertEquals(15, newPosition.score.scoreBlack);
    assertEquals(24, newPosition.score.scoreWhite);

    Position position2 = new Position(dimension);
    position2.setIntersection(45, Stone.BLACK);
    position2.setIntersection(46, Stone.BLACK);
    position2.setIntersection(47, Stone.BLACK);
    position2.setIntersection(56, Stone.BLACK);
    position2.setIntersection(57, Stone.BLACK);
    position2.setIntersection(58, Stone.BLACK);
    position2.setIntersection(65, Stone.BLACK);
    position2.setIntersection(67, Stone.BLACK);
    position2.setIntersection(74, Stone.BLACK);
    position2.setIntersection(76, Stone.BLACK);

    position2.setIntersection(1, Stone.WHITE);
    position2.setIntersection(5, Stone.WHITE);
    position2.setIntersection(10, Stone.WHITE);
    position2.setIntersection(12, Stone.WHITE);
    position2.setIntersection(13, Stone.WHITE);
    position2.setIntersection(14, Stone.WHITE);
    position2.setIntersection(20, Stone.WHITE);
    position2.setIntersection(21, Stone.WHITE);
    position2.score = logic.score(position2);
    System.out.println(position2.toString());
    assertEquals(18, position2.score.scoreBlack);
    assertEquals(12, position2.score.scoreWhite);


    Position position3 = new Position(dimension);
    position3.setIntersection(36, Stone.BLACK);
    position3.setIntersection(45, Stone.BLACK);
    position3.setIntersection(54, Stone.BLACK);
    position3.setIntersection(64, Stone.BLACK);
    position3.setIntersection(65, Stone.BLACK);
    position3.setIntersection(66, Stone.BLACK);
    position3.setIntersection(67, Stone.BLACK);
    position3.setIntersection(68, Stone.BLACK);
    position3.setIntersection(69, Stone.BLACK);
    position3.setIntersection(70, Stone.BLACK);
    position3.setIntersection(62, Stone.BLACK);
    position3.setIntersection(53, Stone.BLACK);
    position3.setIntersection(43, Stone.BLACK);
    position3.setIntersection(34, Stone.BLACK);
    position3.setIntersection(25, Stone.BLACK);
    position3.setIntersection(15, Stone.BLACK);
    position3.setIntersection(14, Stone.BLACK);
    position3.setIntersection(13, Stone.BLACK);
    position3.setIntersection(12, Stone.BLACK);
    position3.setIntersection(11, Stone.BLACK);
    position3.setIntersection(19, Stone.BLACK);
    position3.setIntersection(28, Stone.BLACK);

    position3.setIntersection(20, Stone.WHITE);
    position3.setIntersection(21, Stone.WHITE);
    position3.setIntersection(22, Stone.WHITE);
    position3.setIntersection(23, Stone.WHITE);
    position3.setIntersection(24, Stone.WHITE);
    position3.setIntersection(33, Stone.WHITE);
    position3.setIntersection(42, Stone.WHITE);
    position3.setIntersection(51, Stone.WHITE);
    position3.setIntersection(60, Stone.WHITE);
    position3.setIntersection(59, Stone.WHITE);
    position3.setIntersection(58, Stone.WHITE);
    position3.setIntersection(57, Stone.WHITE);
    position3.setIntersection(56, Stone.WHITE);
    position3.setIntersection(47, Stone.WHITE);
    position3.setIntersection(38, Stone.WHITE);
    position3.setIntersection(29, Stone.WHITE);

    position3.score = logic.score(position3);
    System.out.println(position3.toString());
    assertEquals(51, position3.score.scoreBlack);
    assertEquals(25, position3.score.scoreWhite);

    Position position4 = new Position(dimension);
    position4.setIntersection(9, Stone.BLACK);
    position4.setIntersection(10, Stone.BLACK);
    position4.setIntersection(11, Stone.BLACK);
    position4.setIntersection(21, Stone.BLACK);
    position4.setIntersection(31, Stone.BLACK);
    position4.setIntersection(32, Stone.BLACK);
    position4.setIntersection(41, Stone.BLACK);
    position4.setIntersection(51, Stone.BLACK);
    position4.setIntersection(60, Stone.BLACK);
    position4.setIntersection(69, Stone.BLACK);
    position4.setIntersection(78, Stone.BLACK);
    position4.setIntersection(24, Stone.BLACK);
    position4.setIntersection(16, Stone.BLACK);
    position4.setIntersection(8, Stone.BLACK);

    position4.setIntersection(45, Stone.WHITE);
    position4.setIntersection(46, Stone.WHITE);
    position4.setIntersection(47, Stone.WHITE);
    position4.setIntersection(56, Stone.WHITE);
    position4.setIntersection(66, Stone.WHITE);
    position4.setIntersection(76, Stone.WHITE);

    position4.score = logic.score(position4);
    System.out.println(position4.toString());
    assertEquals(45, position4.score.scoreBlack);
    assertEquals(15, position4.score.scoreWhite);
  }

  @Test
  void testKoRule() {
    dimension = 9;
    logic.dimension = dimension;
    goGame = new GoGame(dimension, players);
    goGame.updateState(new Move(Stone.BLACK, 9));
    goGame.updateState(new Move(Stone.BLACK, 10));
    goGame.updateState(new Move(Stone.BLACK, 11));
    goGame.updateState(new Move(Stone.BLACK, 21));
    goGame.updateState(new Move(Stone.BLACK, 31));
    goGame.updateState(new Move(Stone.BLACK, 32));
    goGame.updateState(new Move(Stone.BLACK, 41));
    goGame.updateState(new Move(Stone.BLACK, 51));
    goGame.updateState(new Move(Stone.BLACK, 60));
    goGame.updateState(new Move(Stone.BLACK, 69));
    goGame.updateState(new Move(Stone.BLACK, 78));
    goGame.updateState(new Move(Stone.BLACK, 24));
    goGame.updateState(new Move(Stone.BLACK, 16));
    goGame.updateState(new Move(Stone.BLACK, 8));

    goGame.updateState(new Move(Stone.WHITE, 45));
    goGame.updateState(new Move(Stone.WHITE, 46));
    goGame.updateState(new Move(Stone.WHITE, 47));
    goGame.updateState(new Move(Stone.WHITE, 56));
    goGame.updateState(new Move(Stone.WHITE, 66));
    goGame.updateState(new Move(Stone.WHITE, 76));

    Position position4 = new Position(dimension);
    position4.setIntersection(9, Stone.BLACK);
    position4.setIntersection(10, Stone.BLACK);
    position4.setIntersection(11, Stone.BLACK);
    position4.setIntersection(21, Stone.BLACK);
    position4.setIntersection(31, Stone.BLACK);
    position4.setIntersection(32, Stone.BLACK);
    position4.setIntersection(41, Stone.BLACK);
    position4.setIntersection(51, Stone.BLACK);
    position4.setIntersection(60, Stone.BLACK);
    position4.setIntersection(69, Stone.BLACK);
    position4.setIntersection(78, Stone.BLACK);
    position4.setIntersection(24, Stone.BLACK);
    position4.setIntersection(16, Stone.BLACK);
    position4.setIntersection(8, Stone.BLACK);

    position4.setIntersection(45, Stone.WHITE);
    position4.setIntersection(46, Stone.WHITE);
    position4.setIntersection(47, Stone.WHITE);
    position4.setIntersection(56, Stone.WHITE);
    position4.setIntersection(66, Stone.WHITE);
    position4.setIntersection(76, Stone.WHITE);

    position4.score = logic.score(position4);
    position4.hash = position4.toString().hashCode();

    assertFalse(logic.checkKoRule(position4, goGame.scorePositionHashMap));
  }

  @Test
  void testCaptures() {
    dimension = 9;
    logic.dimension = dimension;
    goGame = new GoGame(dimension, players);
    goGame.updateState(new Move(Stone.BLACK, 0));
    goGame.updateState(new Move(Stone.WHITE, 1));
    goGame.updateState(new Move(Stone.BLACK, 2));
    goGame.updateState(new Move(Stone.WHITE, 10));
    goGame.updateState(new Move(Stone.BLACK, 11));
    goGame.updateState(new Move(Stone.WHITE, 9));
    goGame.updateState(new Move(Stone.BLACK, 18));
    goGame.updateState(new Move(Stone.WHITE, 0));
    goGame.updateState(new Move(Stone.BLACK, 19));

    System.out.println(goGame.board.currentPosition.toString());
    assertEquals(81, goGame.board.currentPosition.score.scoreBlack);

    goGame.updateState(new Move(Stone.WHITE, 72));
    goGame.updateState(new Move(Stone.BLACK, 10));
    goGame.updateState(new Move(Stone.WHITE, 27));
    goGame.updateState(new Move(Stone.BLACK, 9));
    goGame.updateState(new Move(Stone.WHITE, 28));
    goGame.updateState(new Move(Stone.BLACK, 1));
    goGame.updateState(new Move(Stone.WHITE, 20));
    goGame.updateState(new Move(Stone.BLACK, 30));
    goGame.updateState(new Move(Stone.WHITE, 12));
    goGame.updateState(new Move(Stone.BLACK, 31));
    goGame.updateState(new Move(Stone.WHITE, 3));
    goGame.updateState(new Move(Stone.BLACK, 40));
    goGame.updateState(new Move(Stone.WHITE, 0));

    System.out.println(goGame.board.currentPosition.toString());
    assertEquals(14, goGame.board.currentPosition.score.scoreWhite);
  }
}
