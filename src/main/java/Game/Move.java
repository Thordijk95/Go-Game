package Game;

public class Move {
  public int index;
  public Stone stone;

  public Move(Stone stone, int intersection) {
    this.index = intersection;
    this.stone = stone;
  }
}
