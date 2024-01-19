package Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Class the represents a GoGame, implements the Game interface.
 */
public class GoGame implements Game{

  private GoLogic logic;
  public Board board;

  /**
   * When a game is started the logic is added and the board is created.
   */
  public GoGame(){
    logic = new GoLogic();
    board = new Board();
  }

  // List of old positions
  private List<Position> oldPositions = new LinkedList();
  // Hashmap that links the ith positions to the score
  private HashMap<Integer, Integer> scores = new HashMap<>();

  private void checkMove(Move move) {

  }

  private void checkNewPosition(Position newPosition) {

  }

  public void updatePositions(Position newPosition, int score) {
    oldPositions.add(newPosition);
    logic.score(newPosition);
  }


}
