package Game;

import Game.Player.Player;
import Game.Player.PlayerPlayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.util.Pair;

public class GoLogic {

  /**
   * Calculate the score of the current position.
   * @param position that we want to know the score of
   * @return the score of the position
   */
  public Score score(Position position) {
    Score score = new Score();
    // Calculate the score of both players in the current position
    return score; //TODO return the actual score of the position
  }

  /**
   * Calculate the X and Y coordinates of an intersection.
   * @param index of the intersection of which the x and y need to be known
   * @param dimension of the board, this is not stored in the logic
   * @return an object of the class IntersectionCoordinate that contains and X and Y field
   */
  public int[] calculateXY(int index, int dimension) {
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
   * @param move being played.
   * @return if the move is valid.
   */
  protected boolean validMove(List<Position> oldPositions, Position position, Move move) {
    // Check if the intersection is unoccupied
    return (position.getIntersection(move.index).stone == Stone.NONE && checkKoRule(position, oldPositions));
  }

  /**
   * Check if the newPosition is valid under the ko rule.
   * @param newPosition being proposed by a player
   * @param oldPositions stored in the game
   * @return
   */
  public boolean checkKoRule(Position newPosition, List<Position> oldPositions) {
    for (Position oldPosition : oldPositions){
      if (oldPosition.equalTo(newPosition)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Find all the clusters of stones, cluster is any group of neighboring stones of the same color.
   * @param position of the board.
   * @param dimension of the board.
   * @return a list of clusters
   */
  public List<Cluster> stoneClusters(Position position, int dimension) {
    List<Cluster> clusters = new ArrayList<>();
    for (int i = 0; i < dimension*dimension; i++) {
      int[] coordinate = calculateXY(i, dimension);
      List<int[]> neighborsStraight = findNeighborsStraigh(coordinate, dimension);
      List<int[]> neighbors = findNeighborsDiagnoal(neighborsStraight, coordinate, dimension);
      boolean found = false;
      // Check if the current intersection is taken by one of the players
      if (position.getIntersection(i).stone != Stone.NONE) {
        if (clusters.isEmpty()) { // first cluster found
          Cluster cluster = new Cluster(position.getIntersection(i).stone);
          cluster.intersectionList.add(coordinate);
          defineBorder(position, dimension, neighborsStraight, cluster, coordinate);
          clusters.add(cluster);
        } else {
          // Check if the current intersection is connected to one of the existing clusters and the same stone
          for (int[] neighbor : neighbors) {
            for (Cluster cluster : clusters) {
              for (int[] intersection : cluster.intersectionList) {
                if (Arrays.equals(intersection, neighbor) && position.getIntersection(i).stone == cluster.stone) {
                  cluster.intersectionList.add(coordinate);
                  defineBorder(position, dimension, neighborsStraight, cluster, coordinate);
                  found = true;
                  break;
                } else if (Arrays.equals(intersection, neighbor) && position.getIntersection(i).stone != cluster.stone) {
                  boolean front = true;
                }
              }
            }
            if (found) {break;}
          }
          // All neighbors are part of a different cluster or no cluster, make a new cluster
          if (!found) {
            Cluster cluster = new Cluster(position.getIntersection(i).stone);
            cluster.intersectionList.add(coordinate);
            defineBorder(position, dimension, neighborsStraight, cluster, coordinate);
            clusters.add(cluster);
          }
        }
      }
    }

    return clusters;
  }

  /**
   * Method that defines the border of a cluster.
   */
  public void defineBorder(Position position, int dimension, List<int[]> neighbors, Cluster cluster, int[] coordinate) {
    if (neighbors.size() < 2) { //edge of the board so part of the border of the cluster
      cluster.coordinatesBorder.add(coordinate);
    } else {
      for (int[] neighbor : neighbors) {
        int index = neighbor[1] * dimension + neighbor[0];
        // If any of the neighbors is not of the same stone it is a border
        if (position.getIntersection(index).stone != cluster.stone && index < dimension*dimension
            && !containsArray(cluster.coordinatesBorder, coordinate)) {
          cluster.coordinatesBorder.add(coordinate);
        }
      }
    }
  }

  /**
   * Find all neighboring intersections, if at the edge of the board there is no neighbor.
   * Neighbors are all that neighbor the same 4 squares as the current intersection
   * @param coordinate of the intersection fo which the neighbors are needed
   * @param dimension of the board
   * @return the neighbors
   */
  public List<int[]> findNeighborsStraigh(int[] coordinate, int dimension) {

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
  public List<int[]> findNeighborsDiagnoal(List<int[]> neighbors, int[] coordinate, int dimension) {

    List<int[]> neighborsDiagonal = new ArrayList<>();

    for (int[] neighbor : neighbors) {
      neighborsDiagonal.add(neighbor.clone());
    }

    // Neighbor up left (diagonal)
    if (!Arrays.equals(new int[]{Math.max(0, coordinate[0]-1),  Math.max(0, coordinate[1] - 1)}, coordinate)
      && !containsArray(neighborsDiagonal, new int[]{Math.max(0, coordinate[0]-1),  Math.max(0, coordinate[1] - 1)})) {
      neighborsDiagonal.add(new int[]{Math.max(0, coordinate[0]-1), Math.max(0, coordinate[1] - 1)});
    }

    // Neighbor up right (diagonal)
    if (!Arrays.equals(new int[]{Math.min(dimension - 1, coordinate[0] + 1), Math.max(0, coordinate[1] - 1)}, coordinate)
        && !containsArray(neighborsDiagonal, (new int[]{Math.min(dimension - 1, coordinate[0] + 1), Math.max(0, coordinate[1] - 1)}))) {
      neighborsDiagonal.add(new int[]{Math.min(dimension - 1, coordinate[0] + 1), Math.max(0, coordinate[1] - 1)});
    }

    // Neighbor down left (diagonal)
    if (!Arrays.equals(new int[]{Math.max(0, coordinate[0] - 1), Math.min(dimension - 1, coordinate[1] + 1)}, coordinate)
        && !containsArray(neighborsDiagonal, new int[]{Math.max(0, coordinate[0] - 1), Math.min(dimension - 1, coordinate[1] + 1)})) {
      neighborsDiagonal.add(new int[]{Math.max(0, coordinate[0] - 1), Math.min(dimension - 1, coordinate[1] + 1)});
    }

    // Neighbor down right (diagonal)
    if (!Arrays.equals(new int[]{Math.min(dimension - 1, coordinate[0] + 1), Math.min(dimension - 1, coordinate[1] + 1)}, coordinate)
        && !containsArray(neighborsDiagonal, new int[]{Math.min(dimension - 1, coordinate[0] + 1), Math.min(dimension - 1, coordinate[1] + 1)})) {
      neighborsDiagonal.add(new int[]{Math.min(dimension - 1, coordinate[0] + 1), Math.min(dimension - 1, coordinate[1] + 1)});
    }
    return neighborsDiagonal;
  }

  private boolean containsArray(List<int[]> neighbors, int[] target) {
    for (int[] neighbor : neighbors) {
      if (Arrays.equals(neighbor, target)) {
        return true;
      }
    }
    return false;
  }
}
