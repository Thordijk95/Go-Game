package game;

public class Score {
  public int scoreBlack;
  public int scoreWhite;

  public Score(int scoreBlack, int scoreWhite) {
    this.scoreWhite = scoreWhite;
    this.scoreBlack = scoreBlack;
  }

  @Override
  public String toString() {
    return String.format("Black: " + scoreBlack + "\n" + "White: "+ scoreWhite);
  }

}
