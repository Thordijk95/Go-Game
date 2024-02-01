package game;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

  public Stone stone;
  public List<int[]> intersectionList;
  public List<int[]> coordinatesBorder;
  public List<int[]> territoryList;
  public List<Stone> borderStones;

  public int eyes;

  public Cluster(Stone stone) {
    this.eyes = 0;
    this.stone = stone;
    intersectionList = new ArrayList<>();
    coordinatesBorder = new ArrayList<>();
    territoryList = new ArrayList<>();
    borderStones = new ArrayList<>();
  }
}
