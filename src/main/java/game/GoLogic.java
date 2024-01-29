package game;

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
        // If only one color is found a the border of the none-cluster find cluster it is a territory of.
        if (cluster.borderStones.size() == 1) {
          switch (cluster.borderStones.getFirst()) {
            case BLACK -> assignNoneCluster(position, stoneClusterHashMap.get(Stone.BLACK), cluster);
            case WHITE -> assignNoneCluster(position, stoneClusterHashMap.get(Stone.WHITE), cluster);
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
  //TODO fix this KoRule implementation
  public boolean checkKoRule(Position newPosition, List<Position> oldPositions) {
    for (Position oldPosition : oldPositions) {
      if (oldPosition.equalTo(newPosition)) {
        return false;
      }
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

  public void recursiveCluster(Position position, int index, Cluster cluster, List<int[]> occupiedIntersections) {
    List<int[]> neighbors = findAllNeighborsStraight(calculateXY(index));
    int count = 0;
    for (int[] neighbor : neighbors) {
      if (position.getIntersection(calculateIndex(neighbor)).stone == Stone.NONE) {
        count += 1;
      }
    }
    if (count >= 3) {
      neighbors = findAllNeighborsDiagonal(neighbors, calculateXY(index));
    }
    //
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

  public void assignNoneCluster(Position position, List<Cluster> clusters, Cluster cluster) {
    for (Cluster colorCluster : clusters) {
      List<int[]> neighbors = findAllNeighborsStraight(cluster.intersectionList.getFirst());
      for (int[] neighbor : neighbors) {
        if (containsArray(colorCluster.coordinatesBorder, neighbor)) {
          colorCluster.territoryList.addAll(cluster.intersectionList);
          break;
        }
      }
    }

  }
  public void findTerritory(Position position, int index, Cluster cluster, List<int[]> territory, List<int[]> checkedIntersections) {
    Stone opponent = opponent(cluster.stone);
    List<int[]> neighbors = findAllNeighborsDiagonal((findAllNeighborsStraight(calculateXY(index))), calculateXY(index));
    // For all the neighbors of the current intersection
    for (int[] neighbor : neighbors) {
      // If the any of the neighbors is an opponents than this is not part of the territory
      if (position.getIntersection(calculateIndex(neighbor)).stone == opponent) {
        cluster.territoryList.clear();
      }
    }

  }

//  /**
//   * Find all the clusters of stones, cluster is any group of neighboring stones of the same color.
//   * Replaced by recursiveCluster method
//   * @param position of the board.
//   * @return a HashMap of clusters
//   */
//  public HashMap<Stone, List<Cluster>> stoneClusters(Position position) {
//    HashMap<Stone, List<Cluster>> clusterHashMap = new HashMap<>();
//    for (int i = 0; i < dimension * dimension; i++) {
//      int[] intersectionCoordinate = calculateXY(i);
//      List<int[]> neighborsStraight = findNeighborsStraight(intersectionCoordinate);
//      List<int[]> neighbors = findNeighborsDiagonal(neighborsStraight, intersectionCoordinate);
//      boolean found = false;
//      // Check if the current intersection is taken by one of the players
//      if (position.getIntersection(i).stone != Stone.NONE) {
//        if (clusterHashMap.isEmpty()) { // first cluster found
//          Cluster cluster = new Cluster(position.getIntersection(i).stone);
//          cluster.intersectionList.add(intersectionCoordinate);
//          defineBorder(position, neighborsStraight, cluster, intersectionCoordinate);
//          clusterHashMap.put(cluster.stone, List.of(cluster));
//        } else {
//          // check if the key is already in the HashMap
//          if (clusterHashMap.containsKey(position.getIntersection(i).stone)) {
//            // Check if the current intersection is connected to one of the existing clusters
//            // and has same stone
//            for (int[] neighbor : neighbors) {
//              List<Cluster> clusters = clusterHashMap.get(position.getIntersection(i).stone);
//              for (Cluster cluster : clusters) {
//                for (int[] intersection : cluster.intersectionList) {
//                  if (Arrays.equals(intersection, neighbor)
//                      && position.getIntersection(i).stone == cluster.stone) {
//                    cluster.intersectionList.add(intersectionCoordinate);
//                    defineBorder(position, neighborsStraight, cluster, intersectionCoordinate);
//                    found = true;
//                    break;
//                  }
//                }
//              }
//              if (found) {
//                break;
//              }
//            }
//          }
//          // All neighbors are part of a different cluster or no cluster, make a new cluster
//          if (!found) {
//            Cluster cluster = new Cluster(position.getIntersection(i).stone);
//            cluster.intersectionList.add(intersectionCoordinate);
//            defineBorder(position, neighborsStraight, cluster, intersectionCoordinate);
//            List<Cluster> hashedClusters;
//            List<Cluster> clusterList;
//            if (clusterHashMap.get(cluster.stone) != null) {
//              // Create a copy of the cluster list
//              hashedClusters = clusterHashMap.get(cluster.stone);
//              clusterList = new ArrayList<>(hashedClusters);
//              clusterList.add(cluster);
//            } else {
//              clusterList = List.of(cluster);
//            }
//            clusterHashMap.put(cluster.stone, clusterList);
//          }
//        }
//      }
//    }
//    return clusterHashMap;
//  }

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

  /**
   * Find the territories occupied by the found clusters. (if any)
   * @param clusterHashMap that contains the list of clusters for each of the color stones
   * @param position of the board to search in.
   */
  public void clusterTerritory(HashMap<Stone, List<Cluster>> clusterHashMap, Position position) {
    // Split the clusters based on the color.
    List<Cluster> blackClusters = clusterHashMap.get(Stone.BLACK);
    List<Cluster> whiteClusters = clusterHashMap.get(Stone.WHITE);
    // For each of the clusters of a color find the territory if it exists.
    if (blackClusters != null) {
      for (Cluster tmpCluster : blackClusters) {
        if (clusterHasFreedom(position, tmpCluster)) {
          clusterHasTerritory(position, tmpCluster);
        }
      }
    }
    if (whiteClusters != null) {
      for (Cluster tmpCluster : whiteClusters) {
        if (clusterHasFreedom(position, tmpCluster)) {
          clusterHasTerritory(position, tmpCluster);
        }
      }
    }
  }

  /**
   * Provided a cluster and a board position find the clusters that own a territory on the board.
   * Store the size of the found territory in the cluster.
   * @param position Current position
   * @param cluster being checked.
   */
  private void clusterHasTerritory(Position position, Cluster cluster) {
    List<int[]> territory = new ArrayList<>();
    // A territory or area is an open space from a border to either
    // the same stone or the edge of the board
    for (int i = 0; i < cluster.coordinatesBorder.size(); i++) {
      int freeUp = isFreeUp(cluster.coordinatesBorder.get(i), position);
      int freeLeft = isFreeLeft(cluster.coordinatesBorder.get(i), position);
      int freeDown = isFreeDown(cluster.coordinatesBorder.get(i), position);
      int freeRight = isFreeRight(cluster.coordinatesBorder.get(i), position);
      if (freeUp > 0 || freeLeft > 0) {
        isTerritoryUpLeft(cluster.coordinatesBorder.get(i), position, cluster.stone,
            cluster.intersectionList, territory);
      }
      if (freeDown > 0 || freeLeft > 0) {
        isTerritoryDownLeft(cluster.coordinatesBorder.get(i), position, cluster.stone,
            cluster.intersectionList, territory);
      }
      if (freeUp > 0 || freeRight > 0) {
        isTerritoryUpRight(cluster.coordinatesBorder.get(i), position, cluster.stone,
            cluster.intersectionList, territory);
      }
      if (freeDown > 0 || freeRight > 0) {
        isTerritoryDownRight(cluster.coordinatesBorder.get(i), position, cluster.stone,
            cluster.intersectionList, territory);
      }
    }
    cluster.territoryList = territory;
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
  private boolean isTerritoryUpLeft(int[] intersection, Position position, Stone stone,
      List<int[]> clusterIntersections, List<int[]> territory) {
    Stone opponentStone = opponent(stone);
    Stone currentStone = position.getIntersection(
        position.convertXYtoIndex(intersection, dimension)).stone;

    int[] neighborUp = new int[]{intersection[0], Math.max(intersection[1] - 1, 0)};
    int[] neighborLeft = new int[]{Math.max(intersection[0] - 1, 0), intersection[1]};

    // Last intersection on the board,
    if ((intersection[0] == 0 && intersection[1] == 0) //Check the last corner bottom right not the opponent
        && (currentStone != opponentStone)) {
      if (currentStone == Stone.NONE && !containsArray(clusterIntersections, intersection)
          &&  !containsArray(territory, intersection)) { // Check if the intersection is not part of the cluster (empty)
        territory.add(intersection);
        return true;
      } else { return true; }   // the stone is part of the cluster and not its territory, however the territory still exists.
    } else if (currentStone == opponentStone) { // Opponent encountered, this is not part of the territory, territory is abolished
      territory.clear();
      return false;
    } else if (intersection[0] == 0) { //Right edge of the board reached, go down
      if (!containsArray(territory, intersection) && !containsArray(clusterIntersections, intersection)) {
        territory.add(intersection);
      }
      return isTerritoryUpLeft(neighborUp, position, stone, clusterIntersections, territory);
    } else if ((intersection[1] == 0)) { // Bottom edge of the board reached
      if (!containsArray(territory, intersection) && !containsArray(clusterIntersections, intersection)) { territory.add(intersection); }
      return isTerritoryUpLeft(neighborLeft, position, stone, clusterIntersections, territory);
    } else { // Somewhere on the board, give next two neighbors
      if (!containsArray(territory, intersection)
          && !containsArray(clusterIntersections, intersection)) { territory.add(intersection); }
      return isTerritoryUpLeft(neighborUp, position, stone, clusterIntersections, territory)
          && isTerritoryUpLeft(neighborLeft, position, stone, clusterIntersections, territory);
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
  private boolean isTerritoryUpRight(int[] intersection, Position position, Stone stone,
      List<int[]> clusterIntersections, List<int[]> territory) {
    Stone opponentStone = opponent(stone);
    Stone currentStone = position.getIntersection(
        position.convertXYtoIndex(intersection, dimension)).stone;

    int[] neighborUp = new int[]{intersection[0], Math.max(intersection[1] + 1, 0)};
    int[] neighborRight = new int[]{Math.min(intersection[0] + 1, dimension -1), intersection[1]};

    // Last intersection on the board,
    if ((intersection[0] == dimension - 1 && intersection[1] == 0) //Check the last corner bottom right not the opponent
        && (currentStone != opponentStone)) {
      if (currentStone == Stone.NONE && !containsArray(clusterIntersections, intersection)
          &&  !containsArray(territory, intersection)){ // Check if the intersection is not part of the cluster (empty)
        territory.add(intersection);
        return true;
      } else { return true; }   // the stone is part of the cluster and not its territory, however the territory still exists.
    } else if (currentStone == opponentStone) { // Opponent encountered, this is not part of the territory, territory is abolished
      territory.clear();
      return false;
    } else if (intersection[0] == dimension - 1) { //Right edge of the board reached, go down
      if (!containsArray(territory, intersection) && !containsArray(clusterIntersections, intersection)) {
        territory.add(intersection);
      }
      return isTerritoryUpRight(neighborUp, position, stone, clusterIntersections, territory);
    } else if ((intersection[1] == 0)) { // Bottom edge of the board reached
      if (!containsArray(territory, intersection) && !containsArray(clusterIntersections, intersection)) { territory.add(intersection); }
      return isTerritoryUpRight(neighborRight, position, stone, clusterIntersections, territory);
    } else { // Somewhere on the board, give next two neighbors
      if (!containsArray(territory, intersection)
          && !containsArray(clusterIntersections, intersection)) { territory.add(intersection); }
      return isTerritoryUpRight(neighborUp, position, stone, clusterIntersections, territory)
          && isTerritoryUpRight(neighborRight, position, stone, clusterIntersections, territory);
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
  private boolean isTerritoryDownLeft(int[] intersection, Position position, Stone stone,
      List<int[]> clusterIntersections, List<int[]> territory) {
    Stone opponentStone = opponent(stone);
    Stone currentStone = position.getIntersection(
        position.convertXYtoIndex(intersection, dimension)).stone;

    int[] neighborDown = new int[]{intersection[0], Math.min(intersection[1] + 1, dimension -1)};
    int[] neighborLeft = new int[]{Math.max(intersection[0] - 1, 0), intersection[1]};

    // Last intersection on the board,
    if ((intersection[0] == 0&& intersection[1] == dimension - 1) //Check the last corner bottom right not the opponent
        && (currentStone != opponentStone)) {
      if (currentStone == Stone.NONE && !containsArray(clusterIntersections, intersection)
          &&  !containsArray(territory, intersection)){ // Check if the intersection is not part of the cluster (empty)
        territory.add(intersection);
        return true;
      } else { return true; }   // the stone is part of the cluster and not its territory, however the territory still exists.
    } else if (currentStone == opponentStone) { // Opponent encountered, this is not part of the territory, territory is abolished
      territory.clear();
      return false;
    } else if (intersection[0] == 0) { //Right edge of the board reached, go down
      if (!containsArray(territory, intersection) && !containsArray(clusterIntersections, intersection)) {
        territory.add(intersection);
      }
      return isTerritoryDownLeft(neighborDown, position, stone, clusterIntersections, territory);
    } else if ((intersection[1] == dimension - 1)) { // Bottom edge of the board reached
      if (!containsArray(territory, intersection) && !containsArray(clusterIntersections, intersection)) { territory.add(intersection); }
      return isTerritoryDownLeft(neighborLeft, position, stone, clusterIntersections, territory);
    } else { // Somewhere on the board, give next two neighbors
      if (!containsArray(territory, intersection)
          && !containsArray(clusterIntersections, intersection)) { territory.add(intersection); }
      return isTerritoryDownLeft(neighborDown, position, stone, clusterIntersections, territory)
          && isTerritoryDownLeft(neighborLeft, position, stone, clusterIntersections, territory);
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
  private boolean isTerritoryDownRight(int[] intersection, Position position, Stone stone,
      List<int[]> clusterIntersections, List<int[]> territory) {
    Stone opponentStone = opponent(stone);
    Stone currentStone = position.getIntersection(
        position.convertXYtoIndex(intersection, dimension)).stone;

    int[] neighborDown = new int[]{intersection[0], Math.min(intersection[1] + 1, dimension -1)};
    int[] neighborRight = new int[]{Math.min(intersection[0] + 1, dimension -1), intersection[1]};

    // Last intersection on the board,
    if ((intersection[0] == dimension - 1 && intersection[1] == dimension - 1) //Check the last corner bottom right not the opponent
        && (currentStone != opponentStone)) {
      if (currentStone == Stone.NONE && !containsArray(clusterIntersections, intersection)
          &&  !containsArray(territory, intersection)) { // Check if the intersection is not part of the cluster (empty)
        territory.add(intersection);
        return true;
      } else { return true; }   // the stone is part of the cluster and not its territory, however the territory still exists.
    } else if (currentStone == opponentStone) { // Opponent encountered, this is not part of the territory, territory is abolished
        territory.clear();
        return false;
    } else if (intersection[0] == dimension - 1) { //Right edge of the board reached, go down
        if (!containsArray(territory, intersection) && !containsArray(clusterIntersections, intersection)) {
          territory.add(intersection);
        }
        return isTerritoryDownRight(neighborDown, position, stone, clusterIntersections, territory);
    } else if ((intersection[1] == dimension - 1)) { // Bottom edge of the board reached
        if (!containsArray(territory, intersection) && !containsArray(clusterIntersections, intersection)) { territory.add(intersection); }
        return isTerritoryDownRight(neighborRight, position, stone, clusterIntersections, territory);
    } else { // Somewhere on the board, give next two neighbors
        if (!containsArray(territory, intersection)
            && !containsArray(clusterIntersections, intersection)) { territory.add(intersection); }
        return isTerritoryDownRight(neighborDown, position, stone, clusterIntersections, territory)
            && isTerritoryDownRight(neighborRight, position, stone, clusterIntersections, territory);
    }
  }

  /**
   * Method that checks if a target array is already in a list of arrays.
   *
   * @param arrayList already found
   * @param target    being checked
   * @return if the target is already in the list or not.
   */
  private boolean containsArray(List<int[]> arrayList, int[] target) {
    for (int[] neighbor : arrayList) {
      if (Arrays.equals(neighbor, target)) {
        return true;
      }
    }
    return false;
  }
}
