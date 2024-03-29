package game;

import java.util.ArrayList;
import java.util.List;

// Class that will be used to represent the game state
public class Position {
  // The state of the position is stored as a board
  private List<Intersection> intersectionList;
  public Score score;
  public int hash;

  public Position(int dimension) {
    intersectionList = new ArrayList<>();
    for (int i = 0; i < dimension*dimension; i++) {
      intersectionList.add(new Intersection(i));
    }
    score = new Score(0,0);
    hash = toString().hashCode();
  }

  /**
   * Construct the new position from the old position, adjusting to represent the move.
   * @param oldPosition previous position of the board
   * @param move that was proposed by the player.
   */
  public Position(Position oldPosition, Move move) {
    intersectionList = new ArrayList<>();
    for (int i = 0; i < oldPosition.intersectionList.size(); i++) {
      if (i == move.index) {
        Intersection newIntersection = new Intersection(move.index);
        newIntersection.stone = move.stone;
        intersectionList.add(newIntersection);
      } else {
        intersectionList.add(oldPosition.intersectionList.get(i));
      }
    }
    hash = toString().hashCode();
  }

  public Intersection getIntersection(int index) {
    return intersectionList.get(index);
  }

  /**
   * Set an intersection using its index and the required stone value.
   * @param index of the intersection being set.
   * @param stone of the stone to place at the intersection.
   */
  public void setIntersection(int index, Stone stone) {
    intersectionList.get(index).stone = stone;
    hash = toString().hashCode();
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

  @Override
  public String toString() {
    String position = "";
    int dimension = (int) Math.sqrt(intersectionList.size());
    for (int i = 0; i < intersectionList.size(); i++) {
      if ((i) == 0) { //first line, create border
        position = String.format(position+ upperBorder(dimension) + "|");
      }
      String stone = stoneToString(intersectionList.get(i).stone, i);
      // Check for new row
      if (i % dimension == 0 && i != 0) {
        position = String.format(position + "\n" + border(dimension) + "\n|");
      }
      position = String.format(position + stone);
      if (i == intersectionList.size()-1) { // Last line create lower border
        position = String.format(position + "\n" + lowerBorder(dimension) );
      }
    }

    return position;
  }

  /**
   * Builds a border at the top of the board.
   * @param dimension of the board
   * @return the string that looks like an edge of a board
   */
  private String upperBorder(int dimension) {
    String line = "|";
    for (int j = 0; j < dimension; j++) {
      line = String.format(line + "‾‾‾‾");
    }
    line = String.format(line + "|\n");
    return line;
  }

  /**
   * Builds a border in the middle of the board.
   * @param dimension of the board
   * @return the string that looks like line on the board
   */
  private String border(int dimension) {
    String line = "|";
    for (int i = 0; i < dimension; i++) {
      line = String.format(line + "---");
      if (i != dimension-1) {
        line = String.format(line + "+");
      }
    }
    line = String.format(line + "|");
    return line;
  }
  /**
   * Builds a border at the bottom of the board.
   * @param dimension of the board
   * @return the string that looks like an edge of a board
   */
  private String lowerBorder(int dimension) {
    String line = "|";
    for (int j = 0; j < dimension; j++) {
      line = String.format(line + "____");
    }
    line = String.format(line + "|");
    return line;
  }

  /**
   * Convert the value of a stone to an icon that represents it.
   * @param stone to convert
   * @param i index of the stone, empty intersection are indicated by a number
   * @return the string.
   */
  private String stoneToString(Stone stone, int i) {
    String white = " ● |";
    String black = " ○ |";
    String none = " + |";
    switch (stone) {
      case BLACK -> { return black; }
      case WHITE -> { return white; }
      default -> {return noneToNumber(i); }
    }
  }

  /**
   * Convert a none value to its index with a border.
   * @param i index of the intersection
   * @return the properly configured string
   */
  private String noneToNumber (int i) {
    String stone;
    if (i < 10) {
      stone = String.format(" " + i + " |");
    } else if (i < 100) {
      stone = String.format(" " + i + "|");
    } else {
      stone = String.format(i + "|");
    }
    return stone;
  }

  public static void main(String[] args) {
    Position newPosition = new Position(11);
    System.out.println(newPosition);
  }
}
