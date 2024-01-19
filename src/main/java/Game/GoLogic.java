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
   * Calcualte the score of the current position.
   * @param position that we want to know the score of
   * @return the score of the position
   */
  public int score(Position position) {
    // Calculate the score of both players in the current position
    return 0; //TODO
  }

  public Player getWinner(Game game) {
    // returns the winner of the board connected to the game logic
    return (Player) new PlayerPlayer(); // TODO return the proper player

  }
}
