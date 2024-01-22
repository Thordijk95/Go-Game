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
  public GoGame(int dimension){
    logic = new GoLogic();
    board = new Board(dimension);
  }

  // List of old positions
  private List<Position> previousPositions = new ArrayList<>();
  // Hashmap that links the ith positions to the score
  private HashMap<Integer, Integer> scores = new HashMap<>();

  private void checkMove(Move move) {

  }

  private void checkNewPosition(Position newPosition) {

  }

  public void updatePositions(Position newPosition) {
    // Update the history and the HashMap used for comparing positions
    previousPositions.add(newPosition);
    int score = logic.score(newPosition);
    scores.put(previousPositions.size()+1, score);
    // Update the board
    board.updatePosition(newPosition);
  }


}
