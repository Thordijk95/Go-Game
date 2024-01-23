package Game;

import java.util.List;

/**
 * Class that represents the board where the game is played.
 */
public class Board {
  public int dimension;
  public Position currentPosition;
  private List<Position> previousPositions;

  /**
   * Create a new board.
   * @param dimension of the board, boards are square
   */
  public Board(int dimension) {
    this.dimension = dimension;
    // Create the first position using the constructor that requires the dimension of the board
    currentPosition = new Position(dimension);
  }

  /**
   * Update the current position of the board with the new position.
   * @param newPosition on the board
   */
  public void updatePosition(Position newPosition) {
    currentPosition = newPosition;
  }

  /**
   * return the current position.
   * @return currentPosition
   */
  public Position getCurrentPosition() { return currentPosition; }
}
