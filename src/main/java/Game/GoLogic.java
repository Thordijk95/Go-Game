package Game;

import Game.Player.Player;
import Game.Player.PlayerPlayer;
import java.util.List;

public class GoLogic {

  /**
   * Calculate the score of the current position.
   * @param position that we want to know the score of
   * @return the score of the position
   */
  public Score score(Position position) {
    Score score = new Score();
    // Calculate the score of both players in the current position
    return score; //TODO return the actual score of the position
  }

  /**
   * Calculate the X and Y coordinates of an intersection.
   * @param index of the intersection of which the x and y need to be known
   * @param dimension of the board, this is not stored in the logic
   * @return an object of the class IntersectionCoordinate that contains and X and Y field
   */
  public IntersectionCoordinate calculateXY(int index, int dimension) {
    if (index <= dimension) {
      return new IntersectionCoordinate(index, 0);
    } else {
      int y = index / dimension;
      int x = index % dimension;
      return new IntersectionCoordinate(x,y);
    }
  }

  /**
   * Check if the proposed move is a valid move.
   * @param position of the board.
   * @param move being played.
   * @return if the move is valid.
   */
  protected boolean validMove(List<Position> oldPositions, Position position, Move move) {
    // Check if the intersection is unoccupied
    return (position.getIntersection(move.index).stone == Stone.NONE && checkKoRule(position, oldPositions));
  }

  /**
   * Check if the newPosition is valid under the ko rule.
   * @param newPosition being proposed by a player
   * @param oldPositions stored in the game
   * @return
   */
  public boolean checkKoRule(Position newPosition, List<Position> oldPositions) {
    for (Position oldPosition : oldPositions){
      if (oldPosition.equalTo(newPosition)) {
        return false;
      }
    }
    return true;
  }
}
