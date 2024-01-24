package Game;

public class Move {
  public int index;
  public Stone stone;

  public Move(Stone stone, int index) {
    this.index = index;
    this.stone = stone;
  }
}
