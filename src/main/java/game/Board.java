package game;

/**
 * Class that represents the board where the game is played.
 */
public class Board {
  public int dimension;
  public Position currentPosition;

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
   * @param move on the board
   */
  public void newPosition(Move move) {
    currentPosition = new Position(currentPosition, move);
  }

  /**
   * return the current position.
   * @return currentPosition
   */
  public Position getCurrentPosition() { return currentPosition; }
}
