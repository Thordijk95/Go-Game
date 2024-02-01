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
}
