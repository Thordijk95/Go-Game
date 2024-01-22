package Game;

import java.util.ArrayList;
import java.util.List;

// Class that will be used to represent the game state
public class Position {
  // The state of the position is stored as a board
  private List<Intersection> intersectionList;

  public Position(int dimension) {
    intersectionList = new ArrayList<>();
    for (int i = 0; i < dimension*dimension; i++) {

      intersectionList.add(new Intersection(i));
    }
  }

  /**
   * Construct the new position from the old position, adjusting to represent the move.
   * @param oldPosition previous position of the board
   * @param move that was proposed by the player.
   */
  public Position(Position oldPosition, Move move) {
    intersectionList = new ArrayList<>();
    for (int i = 0; i < oldPosition.intersectionList.size(); i++) {
      if (i == move.intersection.index) {
        intersectionList.add(move.intersection);
      } else {
        intersectionList.add(oldPosition.intersectionList.get(i));
      }
    }
  }

  /**
   * Compare this position to a proposed new position to see if they are equal.
   * @param newPosition that is proposed by a player
   * @return if the positions are equal
   */
  public boolean equalTo(Position newPosition) {
    // Parse all intersections in the new and old position and compare the value, if 1 is different
    // this is a new position
    for (int i = 0; i < intersectionList.size(); i++){
      if (this.intersectionList.get(i).stone != newPosition.intersectionList.get(i).stone) {
        return false;
      }
    }
    // end of the position is reached, so everything was the same and the positions are equal
    return true;
  }
}
