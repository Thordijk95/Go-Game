package Game;

public class Intersection {
  public Stone stone;
  public int index;

  public Intersection(int index) {
    this.index = index;
    stone = Stone.None;
  }

  /**
   * Update what stone, if any, is currently located on this intersection.
   * @param stone that is now in the intersection.
   */
  public void updateIntersection(Stone stone) {
    this.stone = stone;
  }
}
