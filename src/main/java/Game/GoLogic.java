package Game;

import Game.Player.Player;
import Game.Player.PlayerPlayer;
import java.util.List;

public class GoLogic {

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

  /**
   * Calculate the score of the current position.
   * @param position that we want to know the score of
   * @return the score of the position
   */
  public int score(Position position) {
    // Calculate the score of both players in the current position
    return 0; //TODO return the actual score of the position
  }

  public Player getWinner(Game game) {
    // returns the winner of the board connected to the game logic
    return (Player) new PlayerPlayer(); // TODO return the proper player

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
}
