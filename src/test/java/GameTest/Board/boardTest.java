package GameTest.Board;

import static org.junit.Assert.assertEquals;

import Game.Board;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;


public class boardTest {
  private Board testBoard;
  private static final int boardDimension = 9;
  @BeforeEach
  void setup() {
    testBoard = new Board(boardDimension);
  }

  @Test
  void setBoardDimension() {
    assertEquals(testBoard.dimension, boardDimension);
  }

  @Test
  void updateBoard() {

  }
}
