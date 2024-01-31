package game;

import com.nedap.go.exceptions.IntersectionOccupiedException;
import com.nedap.go.exceptions.InvalidMoveException;
import com.nedap.go.exceptions.KoRuleException;
import com.nedap.go.exceptions.MovePositionOutOfBounds;
import com.sun.prism.shape.ShapeRep.InvalidationType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GoLogic {

  public int dimension;

  /**
   * Calculate the score of the current position.
   *
   * @param position that we want to know the score of
   * @return the score of the position
   */
  public Score score(Position position) {
    int scoreBlack = 0;
    int scoreWhite = 0;
    HashMap<Stone, List<Cluster>> stoneClusterHashMap = findClusters(position);
    // For all clusters of None,  find what colors are on the border, if 1 color assign the score to that color.
    if(stoneClusterHashMap.get(Stone.NONE) != null) {
      for (Cluster cluster : stoneClusterHashMap.get(Stone.NONE)) {
        findBorderNoneClusters(position, cluster);
        // If only one color is found a border of the none-cluster find cluster it is a territory of.
        if (cluster.borderStones.size() == 1) {
          switch (cluster.borderStones.getFirst()) {
            case BLACK -> assignNoneCluster(stoneClusterHashMap.get(Stone.BLACK), cluster);
            case WHITE -> assignNoneCluster(stoneClusterHashMap.get(Stone.WHITE), cluster);
          }
        }
      }
    }
    if (stoneClusterHashMap.get(Stone.BLACK) != null) {
      for (Cluster cluster : stoneClusterHashMap.get(Stone.BLACK)) {
        scoreBlack += cluster.territoryList.size();
      }
    }
    if (stoneClusterHashMap.get(Stone.WHITE) != null) {
      for (Cluster cluster : stoneClusterHashMap.get(Stone.WHITE)) {
        scoreWhite += cluster.territoryList.size();
      }
    }
    Score score = new Score(scoreBlack, scoreWhite);
    // Calculate the score of both players in the current position
    return score; //TODO return the actual score of the position
  }

  /**
   * Calculate the X and Y coordinates of an intersection.
   *
   * @param index of the intersection of which the x and y need to be known
   * @return an object of the class IntersectionCoordinate that contains and X and Y field
   */
  public int[] calculateXY(int index) {
    int[] coordinates = new int[2];
    if (index < dimension) {
      coordinates[0] = index;
      coordinates[1] = 0;
      return coordinates;
    } else {
      coordinates[0] = index % dimension;
      coordinates[1] = index / dimension;
      return coordinates;
    }
  }

  public int calculateIndex(int[] coordinate) {
    return coordinate[0] + coordinate[1] * dimension;
  }

  /**
   * Check if the proposed move is a valid move.
   * @param position of the board.
   * @param move     being played.
   * @return if the move is valid.
   */
  //TODO check self capture
  protected boolean validMove(HashMap<String, List<Integer>> oldPositions, Position position,
      Move move) throws InvalidMoveException {
    // Check if the intersection is unoccupied
    if (move.index > (dimension*dimension-1)) {
      throw new MovePositionOutOfBounds();
    }
    if( position.getIntersection(move.index).stone != Stone.NONE ) {
      throw new IntersectionOccupiedException();
    }
    Position potentialPosition = new Position(position, move);
    if (!checkKoRule(potentialPosition, oldPositions)){
      throw new KoRuleException();
    }

    return true;
  }

  /**
   * Check if the newPosition is valid under the ko rule.
   *
   * @param newPosition  being proposed by a player
   * @param oldPositions stored in the game
   * @return if the new position adheres to the KO rule
   */
  //TODO fix this KoRule implementation
  public boolean checkKoRule(Position newPosition, HashMap<String, List<Integer>> oldPositions) {
    try {
      List<Integer> sameScorePositions = oldPositions.get(newPosition.score.toString());
      if (sameScorePositions.contains(newPosition.hash)) {
        return false;
      }
    } catch (NullPointerException e) {
      //
    }
    return true;
  }

//  public boolean checkSelfCapture(Position potentialPosition, Move move) {
//    // check if all neighbors are either unoccupied or of the same color
//    // Self capture is allowed provided that it resulted in a capture.
//    List<int[]> neighbors = findNeighborsStraight(calculateXY(move.index));
//    for (int[] neighbor : neighbors ) {
//      if (potentialPosition.getIntersection(neighbor).stone ==
//    }
//  }

  /**
   * Find the clusters in a given board position.
   * @param position of the board
   * @return a HashMap that contains all the clusters, key of the hashmap is the color
   */
  public HashMap<Stone, List<Cluster>> findClusters(Position position) {
    HashMap<Stone, List<Cluster>> clusterHashMap = new HashMap<>();
    List<Cluster> blackClusters = new ArrayList<>();
    List<Cluster> whiteClusters = new ArrayList<>();
    List<Cluster> noneClusters = new ArrayList<>();
    List<int[]> occupiedIntersections = new ArrayList<>();

    // Parse all the fields in the position
    // When stone is encountered start recursion from that point to accumulate all the stones
    for(int i = 0; i < dimension * dimension; i++) {
      // Start of a cluster has been found and the current intersection has not already been evaluated
      //if (position.getIntersection(i).stone != Stone.NONE && !containsArray(occupiedIntersections, calculateXY(i))) {
      if (!containsArray(occupiedIntersections, calculateXY(i))) {
        // Find the first neighbors from that point
        Cluster cluster = new Cluster(position.getIntersection(i).stone);
        cluster.intersectionList.add(calculateXY(i));
        recursiveCluster(position, i, cluster, occupiedIntersections);
        findBorder(position, cluster);
        List<int[]> checkedIntersections = new ArrayList<>();
        switch (cluster.stone) {
          case BLACK -> blackClusters.add(cluster);
          case WHITE -> whiteClusters.add(cluster);
          case NONE -> noneClusters.add(cluster);
        }
      }
    }
    clusterHashMap.put(Stone.BLACK, blackClusters);
    clusterHashMap.put(Stone.WHITE, whiteClusters);
    clusterHashMap.put(Stone.NONE, noneClusters);
    return clusterHashMap;
  }

  /**
   * Recursively construct clusters of stones.
   * @param position of the board
   * @param index to start the recursive search from
   * @param cluster to construct
   * @param occupiedIntersections that are already part of a different cluster.
   */
  public void recursiveCluster(Position position, int index, Cluster cluster, List<int[]> occupiedIntersections) {
    List<int[]> neighbors = new ArrayList<>();
    if (cluster.stone == Stone.NONE) {
      neighbors = findAllNeighborsStraight(calculateXY(index));
      int count = 0;
      for (int[] neighbor : neighbors) {
        if (position.getIntersection(calculateIndex(neighbor)).stone == Stone.NONE) {
          count += 1;
        }
      }
      if (count >= 3) {
        neighbors = findAllNeighborsDiagonal(neighbors, calculateXY(index));
      }
    } else {
      neighbors = findAllNeighborsDiagonal(findAllNeighborsStraight(calculateXY(index)), calculateXY(index));
    }

    if(neighbors != null) {
      for (int[] neighbor : neighbors) {
        if (position.getIntersection(calculateIndex(neighbor)).stone == position.getIntersection(
            index).stone && !containsArray(cluster.intersectionList, neighbor)) {
          cluster.intersectionList.add(neighbor);
          occupiedIntersections.add(neighbor);
          recursiveCluster(position, calculateIndex(neighbor), cluster, occupiedIntersections);
        }
      }
    }
  }

  /**
   * Given a cluster and a position find the border of the cluster using the intersections that are part of the cluster.
   * @param position of the board
   * @param cluster to find the border of
   */
  public void findBorder(Position position, Cluster cluster) {
    for (int[] intersection : cluster.intersectionList) {
      List<int[]> neighbors = findAllNeighborsDiagonal(findAllNeighborsStraight(intersection), intersection);
      if (neighbors.size() > 1) {
        for (int[] neighbor : neighbors) {
          if(position.getIntersection(calculateIndex(neighbor)).stone != cluster.stone
              && !containsArray(cluster.coordinatesBorder, intersection)) {
            cluster.coordinatesBorder.add(intersection);
            break;
          }
        }
      } else {
        System.out.println("sanity check, should never occur");
      }
    }
  }

  /**
   * Find the border of a none cluster, only the color is needed.
   * @param position of the board in which the search is done
   * @param cluster for which the bordering colors are evaluated.
   */
  public void findBorderNoneClusters(Position position, Cluster cluster) {
    for (int[] intersection : cluster.coordinatesBorder) {
      List<int[]> neighbors = findAllNeighborsStraight(intersection);
      for (int[] neighbor : neighbors) {
        if (!cluster.borderStones.contains(position.getIntersection(calculateIndex(neighbor)).stone)
            && position.getIntersection(calculateIndex(neighbor)).stone != Stone.NONE) {
          cluster.borderStones.add(position.getIntersection(calculateIndex(neighbor)).stone);
        }
      }
    }
  }

  /**
   * Assign a cluster of empyty intersections as the territory of another cluster.
   * @param clusters that the none cluster could be a territory of
   * @param noneCluster to assign as a territory
   */
  public void assignNoneCluster(List<Cluster> clusters, Cluster noneCluster) {
    boolean found = false;
    for (Cluster colorCluster : clusters) {
      // Look through all the intersection in the noneCluster to find the neighboring cluster that it is a territory of.
      for (int i = 0; i < noneCluster.intersectionList.size(); i++) {
        List<int[]> neighbors = findAllNeighborsStraight(noneCluster.intersectionList.get(i));
        for (int[] neighbor : neighbors) {
          if (containsArray(colorCluster.coordinatesBorder, neighbor)) {
            colorCluster.territoryList.addAll(noneCluster.intersectionList);
            found = true;
            break;
          }
        }
        if (found) break;
      }
    }

  }

  /**
   * Find all neighboring intersections, if at the edge of the board there is no neighbor. Neighbors
   * are all that neighbor the same 4 squares as the current intersection
   *
   * @param coordinate of the intersection fo which the neighbors are needed
   * @return the neighbors
   */
  public List<int[]> findAllNeighborsStraight(int[] coordinate) {

    List<int[]> neighbors = new ArrayList<>();

    // Neighbor left
    if (!Arrays.equals(new int[]{Math.max(coordinate[0] - 1, 0), coordinate[1]},
        coordinate)) {
      neighbors.add(new int[]{Math.max(coordinate[0] - 1, 0), coordinate[1]});
    }
    // Neighbor right
    if (!Arrays.equals(new int[]{Math.min(coordinate[0] + 1, dimension - 1), coordinate[1]},
        coordinate)) {
      neighbors.add(new int[]{Math.min(coordinate[0] + 1, dimension - 1), coordinate[1]});
    }
    // Neighbor up
    if (!Arrays.equals(new int[]{coordinate[0], Math.max(coordinate[1] - 1, 0)}, coordinate)) {
      neighbors.add(new int[]{coordinate[0], Math.max(0, coordinate[1] - 1)});
    }
    // Neighbor down
    if (!Arrays.equals(new int[]{coordinate[0], Math.min(dimension - 1, coordinate[1] + 1)},
        coordinate)) {
      neighbors.add(new int[]{coordinate[0], Math.min(dimension - 1, coordinate[1] + 1)});
    }
    return neighbors;
  }

  /**
   * Find the neighbors diagnoally from a point and add them to a list of neighbors.
   * @param neighbors already in the list
   * @param coordinate to find the neighbors of
   * @return the neighbors found, keeping the edge of the board in mind
   */
  public List<int[]> findAllNeighborsDiagonal(List<int[]> neighbors, int[] coordinate) {

    List<int[]> neighborsDiagonal = new ArrayList<>();

    for (int[] neighbor : neighbors) {
      neighborsDiagonal.add(neighbor.clone());
    }

    // Neighbor up left (diagonal)
    if (!Arrays.equals(new int[]{Math.max(0, coordinate[0] - 1), Math.max(0, coordinate[1] - 1)},
        coordinate)
        && !containsArray(neighborsDiagonal,
        new int[]{Math.max(0, coordinate[0] - 1), Math.max(0, coordinate[1] - 1)})) {
      neighborsDiagonal.add(
          new int[]{Math.max(0, coordinate[0] - 1), Math.max(0, coordinate[1] - 1)});
    }

    // Neighbor up right (diagonal)
    if (!Arrays.equals(
        new int[]{Math.min(dimension - 1, coordinate[0] + 1), Math.max(0, coordinate[1] - 1)},
        coordinate)
        && !containsArray(neighborsDiagonal,
        new int[]{Math.min(dimension - 1, coordinate[0] + 1), Math.max(0, coordinate[1] - 1)})) {
      neighborsDiagonal.add(
          new int[]{Math.min(dimension - 1, coordinate[0] + 1), Math.max(0, coordinate[1] - 1)});
    }

    // Neighbor down left (diagonal)
    if (!Arrays.equals(
        new int[]{Math.max(0, coordinate[0] - 1), Math.min(dimension - 1, coordinate[1] + 1)},
        coordinate)
        && !containsArray(neighborsDiagonal,
        new int[]{Math.max(0, coordinate[0] - 1), Math.min(dimension - 1, coordinate[1] + 1)})) {
      neighborsDiagonal.add(
          new int[]{Math.max(0, coordinate[0] - 1), Math.min(dimension - 1, coordinate[1] + 1)});
    }

    // Neighbor down right (diagonal)
    if (!Arrays.equals(new int[]{Math.min(dimension - 1, coordinate[0] + 1),
        Math.min(dimension - 1, coordinate[1] + 1)}, coordinate)
        && !containsArray(neighborsDiagonal, new int[]{Math.min(dimension - 1, coordinate[0] + 1),
        Math.min(dimension - 1, coordinate[1] + 1)})) {
      neighborsDiagonal.add(new int[]{Math.min(dimension - 1, coordinate[0] + 1),
          Math.min(dimension - 1, coordinate[1] + 1)});
    }
    return neighborsDiagonal;
  }

  /**
   * Method that checks if a target array is already in a list of arrays.
   *
   * @param arrayList already found
   * @param target    being checked
   * @return if the target is already in the list or not.
   */
  public boolean containsArray(List<int[]> arrayList, int[] target) {
    for (int[] neighbor : arrayList) {
      if (Arrays.equals(neighbor, target)) {
        return true;
      }
    }
    return false;
  }
}
