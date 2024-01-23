package Game;

public class Score {
  public int pointsBlack;
  public int pointsWhite;

  public String toString() {
    return String.format(pointsBlack + "," + pointsWhite);
  }

}
