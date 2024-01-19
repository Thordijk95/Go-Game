package Game;

public class Move {
  public Intersection intersection;
  public Stone stone;

  public Move(Stone stone, int intersection) {
    this.intersection = new Intersection(intersection);
    this.intersection.stone = stone;
  }
}
