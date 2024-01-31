package gametest.board;

import game.GoLogic;
import game.Move;
import game.Position;
import game.Stone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PositionTest {
  
  private int dimension;
  private GoLogic logic;

  @BeforeEach
  void setup() {
    logic = new GoLogic();
    dimension = 9;
  }
  @Test
  void testHasH() {
    Position position = new Position(dimension);
    position.setIntersection(9, Stone.BLACK);
    position.setIntersection(10, Stone.BLACK);
    position.setIntersection(11, Stone.BLACK);
    position.setIntersection(21, Stone.BLACK);
    position.setIntersection(31, Stone.BLACK);
    position.setIntersection(32, Stone.BLACK);
    position.setIntersection(41, Stone.BLACK);
    position.setIntersection(51, Stone.BLACK);
    position.setIntersection(60, Stone.BLACK);
    position.setIntersection(69, Stone.BLACK);
    position.setIntersection(78, Stone.BLACK);
    position.setIntersection(24, Stone.BLACK);
    position.setIntersection(16, Stone.BLACK);
    position.setIntersection(8, Stone.BLACK);

    position.setIntersection(45, Stone.WHITE);
    position.setIntersection(46, Stone.WHITE);
    position.setIntersection(47, Stone.WHITE);
    position.setIntersection(56, Stone.WHITE);
    position.setIntersection(66, Stone.WHITE);
    position.setIntersection(76, Stone.WHITE);

    Position position1 = new Position(position, new Move(Stone.NONE, 82));
    Position position2 = new Position(position, new Move(Stone.NONE, 82));

    Assertions.assertEquals(position1.hash, position2.hash);
  }
}
