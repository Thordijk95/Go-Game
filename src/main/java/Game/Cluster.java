package Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cluster {
  public Stone stone;
  public List<int[]> intersectionList;
  public List<int[]> coordinatesBorder;

  public boolean hasFreedom;

  public Cluster(Stone stone) {
    this.stone = stone;
    intersectionList = new ArrayList<>();
    coordinatesBorder = new ArrayList<>();
    hasFreedom = true;
  }


}
