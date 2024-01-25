package Game;

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
    HashMap<Stone, List<Cluster>> stoneClusterHashMap;
    stoneClusterHashMap = stoneClusters(position);
    List<Cluster> blackClusters = stoneClusterHashMap.get(Stone.BLACK);
    List<Cluster> whiteClusters = stoneClusterHashMap.get(Stone.WHITE);

    Score score = new Score(0, 0);
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

  /**
   * Check if the proposed move is a valid move.
   * @param position of the board.
   * @param move     being played.
   * @return if the move is valid.
   */
  protected boolean validMove(List<Position> oldPositions, Position position, Move move) {
    // Check if the intersection is unoccupied
    return position.getIntersection(move.index).stone == Stone.NONE && checkKoRule(position,
        oldPositions);
  }

  /**
   * Check if the newPosition is valid under the ko rule.
   *
   * @param newPosition  being proposed by a player
   * @param oldPositions stored in the game
   * @return if the new position adheres to the KO rule
   */
  public boolean checkKoRule(Position newPosition, List<Position> oldPositions) {
    for (Position oldPosition : oldPositions) {
      if (oldPosition.equalTo(newPosition)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Find all the clusters of stones, cluster is any group of neighboring stones of the same color.
   *
   * @param position of the board.
   * @return a HashMap of clusters
   */
  public HashMap<Stone, List<Cluster>> stoneClusters(Position position) {
    HashMap<Stone, List<Cluster>> clusterHashMap = new HashMap<>();
    for (int i = 0; i < dimension * dimension; i++) {
      int[] intersectionCoordinate = calculateXY(i);
      List<int[]> neighborsStraight = findNeighborsStraight(intersectionCoordinate);
      List<int[]> neighbors = findNeighborsDiagonal(neighborsStraight, intersectionCoordinate);
      boolean found = false;
      // Check if the current intersection is taken by one of the players
      if (position.getIntersection(i).stone != Stone.NONE) {
        if (clusterHashMap.isEmpty()) { // first cluster found
          Cluster cluster = new Cluster(position.getIntersection(i).stone);
          cluster.intersectionList.add(intersectionCoordinate);
          defineBorder(position, neighborsStraight, cluster, intersectionCoordinate);
          clusterHashMap.put(cluster.stone, List.of(cluster));
        } else {
          // check if the key is already in the HashMap
          if (clusterHashMap.containsKey(position.getIntersection(i).stone)) {
            // Check if the current intersection is connected to one of the existing clusters
            // and has same stone
            for (int[] neighbor : neighbors) {
              List<Cluster> clusters = clusterHashMap.get(position.getIntersection(i).stone);
              for (Cluster cluster : clusters) {
                for (int[] intersection : cluster.intersectionList) {
                  if (Arrays.equals(intersection, neighbor)
                      && position.getIntersection(i).stone == cluster.stone) {
                    cluster.intersectionList.add(intersectionCoordinate);
                    defineBorder(position, neighborsStraight, cluster, intersectionCoordinate);
                    found = true;
                    break;
                  }
                }
              }
              if (found) {
                break;
              }
            }
          }
          // All neighbors are part of a different cluster or no cluster, make a new cluster
          if (!found) {
            Cluster cluster = new Cluster(position.getIntersection(i).stone);
            cluster.intersectionList.add(intersectionCoordinate);
            defineBorder(position, neighborsStraight, cluster, intersectionCoordinate);
            List<Cluster> hashedClusters;
            List<Cluster> clusterList;
            if (clusterHashMap.get(cluster.stone) != null) {
              // Create a copy of the cluster list
              hashedClusters = clusterHashMap.get(cluster.stone);
              clusterList = new ArrayList<>(hashedClusters);
              clusterList.add(cluster);
            } else {
              clusterList = List.of(cluster);
            }
            clusterHashMap.put(cluster.stone, clusterList);
          }
        }
      }
    }

    // After finding the clusters find the territory they might encompass
    List<Cluster> blackClusters = clusterHashMap.get(Stone.BLACK);
    List<Cluster> whiteClusters = clusterHashMap.get(Stone.WHITE);

    for (Cluster tmpCluster : blackClusters) {
      if (clusterHasFreedom(position, tmpCluster)) {
        tmpCluster.hasTerritory = clusterHasTerritory(position, tmpCluster);
      }
    }
    for (Cluster tmpCluster : whiteClusters) {
      if (clusterHasFreedom(position, tmpCluster)) {
        tmpCluster.hasTerritory = clusterHasTerritory(position, tmpCluster);
      }
    }
    return clusterHashMap;
  }

  /**
   * Define the border of cluster.
   *
   * @param position     on the board, required to check the values of the neighbors. not in the
   *                     cluster
   * @param neighbors    of the coordinate being checked
   * @param cluster      that the coordinate is a part of.
   * @param intersection being checked.
   */
  public void defineBorder(Position position, List<int[]> neighbors, Cluster cluster,
      int[] intersection) {
    if (neighbors.size() <= 2) { //edge of the board so part of the border of the cluster
      cluster.coordinatesBorder.add(intersection);
    } else {
      for (int[] neighbor : neighbors) {
        int index = neighbor[1] * dimension + neighbor[0];
        // If any of the neighbors is not of the same stone it is a border
        if (position.getIntersection(index).stone != cluster.stone && index < dimension * dimension
            && !containsArray(cluster.coordinatesBorder, intersection)) {
          cluster.coordinatesBorder.add(intersection);
        }
      }
    }
  }

  private boolean clusterHasTerritory(Position position, Cluster cluster) {
    boolean hasTerritory = false;
    // A territory or area is an open space from a border to either
    // the same stone or the edge of the board
    for (int i = 0; i < cluster.coordinatesBorder.size(); i++) {
      int freeUp = isFreeUp(cluster.coordinatesBorder.get(i), position);
      int freeLeft = isFreeLeft(cluster.coordinatesBorder.get(i), position);
      int freeDown = isFreeUp(cluster.coordinatesBorder.get(i), position);
      int freeRight = isFreeLeft(cluster.coordinatesBorder.get(i), position);
      if (freeUp > 0 && freeLeft > 0) {
        hasTerritory = isTerritoryUpLeft(cluster.coordinatesBorder.get(i), position, cluster.stone);
      }
      if (freeDown > 0 && freeLeft > 0) {
        hasTerritory = isTerritoryDownLeft(cluster.coordinatesBorder.get(i), position,
            cluster.stone);
      }
      if (freeUp > 0 && freeRight > 0) {
        hasTerritory = isTerritoryUpRight(cluster.coordinatesBorder.get(i), position,
            cluster.stone);
      }
      if (freeDown > 0 && freeRight > 0) {
        hasTerritory = isTerritoryDownRight(cluster.coordinatesBorder.get(i), position, cluster.stone);
      }
    }
    return hasTerritory;
  }

  private boolean clusterHasFreedom(Position position, Cluster cluster) {
    int borderSize = cluster.coordinatesBorder.size();
    cluster.hasFreedom = false;

    // Parse the positions in the border of the cluster.
    // For every position check if they have a free space beside them
    // a stone in the same cluster.
    for (int i = 0; i < borderSize; i++) {
      if (isFreeUp(cluster.coordinatesBorder.get(i), position) > 0
          || (isFreeDown(cluster.coordinatesBorder.get(i), position) > 0)
          || (isFreeLeft(cluster.coordinatesBorder.get(i), position) > 0)
          || (isFreeRight(cluster.coordinatesBorder.get(i), position) > 0)) {
        cluster.hasFreedom = true;
      }
    }
    return cluster.hasFreedom;
  }

  /**
   * Find all neighboring intersections, if at the edge of the board there is no neighbor. Neighbors
   * are all that neighbor the same 4 squares as the current intersection
   *
   * @param coordinate of the intersection fo which the neighbors are needed
   * @return the neighbors
   */
  public List<int[]> findNeighborsStraight(int[] coordinate) {

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

  public List<int[]> findNeighborsDiagonal(List<int[]> neighbors, int[] coordinate) {

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
   * Get the opponents stone.
   *
   * @param stone of the current player
   * @return stone of the opponent.
   */
  public Stone opponent(Stone stone) {
    Stone opponent = Stone.NONE;
    switch (stone) {
      case BLACK -> opponent = Stone.WHITE;
      case WHITE -> opponent = Stone.BLACK;
    }
    return opponent;
  }

  /**
   * From an intersection look straight up to see if the stone defines a territory.
   *
   * @param intersection of the stone in a cluster being checked
   * @param position     the current position
   * @return if the intersection has a clear line of sight to the edge or a stone of the same color
   */
  public int isFreeUp(int[] intersection, Position position) {
    int freeCount = 0;
    for (int i = intersection[1]; i > 0; i--) {
      int index = position.convertXYtoIndex(new int[]{intersection[0], i - 1}, dimension);
      // If an opponents stone is found than the territory does not extend up
      if (position.getIntersection(index).stone != Stone.NONE) {
        break;
      } else {
        freeCount += 1;
      }
    }
    return freeCount;
  }

  /**
   * Same as isFreeUp, but looks down.
   *
   * @param intersection of the stone in a cluster being checked
   * @param position     the current position
   * @return if the sight is clear
   */
  public int isFreeDown(int[] intersection, Position position) {
    int freeCount = 0;
    for (int i = intersection[1]; i < dimension - 1; i++) {
      int index = position.convertXYtoIndex(new int[]{intersection[0], i + 1}, dimension);
      // If an opponents stone is found than the territory does not extend up
      if (position.getIntersection(index).stone != Stone.NONE) {
        break;
      } else {
        freeCount += 1;
      }
    }
    return freeCount;
  }

  /**
   * Same as isFreeUp, but looks left.
   *
   * @param intersection of the stone in a cluster being checked
   * @param position     the current position
   * @return if the sight is clear
   */
  public int isFreeLeft(int[] intersection, Position position) {
    int freeCount = 0;
    for (int i = intersection[0]; i > 0; i--) {
      int index = position.convertXYtoIndex(new int[]{i - 1, intersection[1]}, dimension);
      // If an opponents stone is found than the territory does not extend up
      if (position.getIntersection(index).stone != Stone.NONE) {
        break;
      } else {
        freeCount += 1;
      }
    }
    return freeCount;
  }

  /**
   * Same as isFreeUp, but looks right.
   *
   * @param intersection of the stone in a cluster being checked
   * @param position     the current position
   * @return if the sight is clear
   */
  public int isFreeRight(int[] intersection, Position position) {
    int freeCount = 0;
    for (int i = intersection[0]; i < dimension - 1; i++) {
      int index = position.convertXYtoIndex(new int[]{i + 1, intersection[1]}, dimension);
      // If an opponents stone is found than the territory does not extend up
      if (position.getIntersection(index).stone != Stone.NONE) {
        break;
      } else {
        freeCount += 1;
      }
    }
    return freeCount;
  }

  /**
   * Recursive method that checks if the board to the left and above of the current border
   * intersection is part of the territory.
   *
   * @param intersection to start checking at
   * @param position     currently on the board
   * @param stone        being played
   * @return if the upper left quadrant starting from the intersection is part of the territory
   */
  private boolean isTerritoryUpLeft(int[] intersection, Position position, Stone stone) {
    Stone opponentStone = opponent(stone);
    Stone currentStone = position.getIntersection(
        position.convertXYtoIndex(intersection, dimension)).stone;
    // if the recursion method finds the border of the board and the stone is not that of the
    // opponent
    // the square is part of the possible territory
    // Recursive part, if the edge of the board is reached and the intersection is not occupied by
    // the opponent than this is part of your
    // potential territory.
    if ((intersection[0] == 0 || intersection[0] == dimension - 1 || intersection[1] == 0
        || intersection[1] == dimension - 1)
        && (currentStone != opponentStone)) {
      return true;
    } else if (currentStone == opponentStone) {
      return false;
    } else {
      int[] neighborUp = new int[]{intersection[0], Math.max(intersection[1] - 1, 0)};
      int[] neighborLeft = new int[]{Math.max(intersection[0] - 1, 0), intersection[1]};
      return isTerritoryUpLeft(neighborUp, position, stone) && isTerritoryUpLeft(neighborLeft,
          position, stone);
    }
  }

  /**
   * Recursive method that checks if the board to the left and above of the current border
   * intersection is part of the territory.
   *
   * @param intersection to start checking at
   * @param position     currently on the board
   * @param stone        being played
   * @return if the upper left quadrant starting from the intersection is part of the territory
   */
  private boolean isTerritoryUpRight(int[] intersection, Position position, Stone stone) {
    Stone opponentStone = opponent(stone);
    Stone currentStone = position.getIntersection(
        position.convertXYtoIndex(intersection, dimension)).stone;
    // if the recursion method finds the border of the board
    // and the stone is not that of the opponent
    // the square is part of the possible territory
    // Recursive part, if the edge of the board is reached and the intersection is not
    // occupied by the opponent than this is part of your
    // potential territory.
    if ((intersection[0] == 0 || intersection[0] == dimension - 1 || intersection[1] == 0
        || intersection[1] == dimension - 1)
        && (currentStone != opponentStone)) {
      return true;
    } else if (currentStone == opponentStone) {
      return false;
    } else {
      int[] neighborUp = new int[]{intersection[0], Math.max(intersection[1] - 1, 0)};
      int[] neighborRight = new int[]{Math.min(intersection[0] + 1, dimension - 1),
          intersection[1]};
      return isTerritoryUpRight(neighborUp, position, stone) && isTerritoryUpRight(neighborRight,
          position, stone);
    }
  }

  /**
   * Recursive method that checks if the board to the left and above of the current border
   * intersection is part of the territory.
   *
   * @param intersection to start checking at
   * @param position     currently on the board
   * @param stone        being played
   * @return if the upper left quadrant starting from the intersection is part of the territory
   */
  private boolean isTerritoryDownLeft(int[] intersection, Position position, Stone stone) {
    Stone opponentStone = opponent(stone);
    Stone currentStone = position.getIntersection(
        position.convertXYtoIndex(intersection, dimension)).stone;
    // if the recursion method finds the border of the board and the stone is not that
    // of the opponent
    // the square is part of the possible territory
    // Recursive part, if the edge of the board is reached and the intersection is not occupied by
    // the opponent than this is part of your
    // potential territory.
    if ((intersection[0] == 0 || intersection[0] == dimension - 1 || intersection[1] == 0
        || intersection[1] == dimension - 1)
        && (currentStone != opponentStone)) {
      return true;
    } else if (currentStone == opponentStone) {
      return false;
    } else {
      int[] neighborDown = new int[]{intersection[0], Math.max(intersection[1] - 1, 0)};
      int[] neighborLeft = new int[]{Math.max(intersection[0] - 1, 0), intersection[1]};
      return isTerritoryDownLeft(neighborDown, position, stone) && isTerritoryDownLeft(
          neighborLeft, position, stone);
    }
  }

  /**
   * Recursive method that checks if the board to the left and above of the current border
   * intersection is part of the territory.
   *
   * @param intersection to start checking at
   * @param position     currently on the board
   * @param stone        being played
   * @return if the upper left quadrant starting from the intersection is part of the territory
   */
  private boolean isTerritoryDownRight(int[] intersection, Position position, Stone stone) {
    Stone opponentStone = opponent(stone);
    Stone currentStone = position.getIntersection(
        position.convertXYtoIndex(intersection, dimension)).stone;
    // if the recursion method finds the border of the board and the stone is not that of the
    // opponent
    // the square is part of the possible territory
    // Recursive part, if the edge of the board is reached and the intersection is not occupied by the opponent than this is part of your
    // potential territory.
    if ((intersection[0] == 0 || intersection[0] == dimension - 1 || intersection[1] == 0
        || intersection[1] == dimension - 1)
        && (currentStone != opponentStone)) {
      return true;
    } else if (currentStone == opponentStone) {
      return false;
    } else {
      int[] neighborDown = new int[]{intersection[0], Math.max(intersection[1] + 1, 0)};
      int[] neighborRight = new int[]{Math.max(intersection[0] + 1, 0), intersection[1]};
      return isTerritoryDownRight(neighborDown, position, stone) && isTerritoryDownRight(
          neighborRight, position, stone);
    }
  }

  /**
   * Method that checks if a target array is already in a list of arrays.
   *
   * @param neighbors already found
   * @param target    being checked
   * @return if the target is already in the list or not.
   */
  private boolean containsArray(List<int[]> neighbors, int[] target) {
    for (int[] neighbor : neighbors) {
      if (Arrays.equals(neighbor, target)) {
        return true;
      }
    }
    return false;
  }
}
