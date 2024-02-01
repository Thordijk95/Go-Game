package game;

import com.nedap.go.exceptions.InvalidMoveException;
import com.nedap.go.gui.GoGui;
import com.nedap.go.gui.GoGuiIntegrator;
import com.nedap.go.gui.InvalidCoordinateException;
import connectivity.server.ConnectionHandler;
import game.player.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class the represents a GoGame, implements the Game interface.
 */
public class GoGame implements Game {

  private ConnectionHandler turn;
  private GoLogic logic;
  public Board board;
  public int consecutivePasses;
  public List<ConnectionHandler> players;
  // Hashmap that links the ith positions to the score
  public HashMap<Integer, Score> scores = new HashMap<>();
  // Hashmap with a list of Hashed positions that can be lookedup using their score.
  public HashMap<String, List<Integer>> scorePositionHashMap = new HashMap<>();

  private GoGui goGui;

  /**
   * Specific constructor for a game owned by a single player.
   * @param dimension of the board
   */
  public GoGame(int dimension, Player player) {
    board = new Board(dimension);
    logic = new GoLogic();
    logic.dimension = dimension;
    consecutivePasses = 0;
    goGui = new GoGuiIntegrator(true, false, dimension);
    goGui.startGUI();
  }

  /**
   * Specific constructor for a game owned by a single player.
   * @param dimension of the board
   */
  public GoGame(int dimension) {
    board = new Board(dimension);
    logic = new GoLogic();
    logic.dimension = dimension;
    consecutivePasses = 0;
    goGui = new GoGuiIntegrator(true, false, dimension);
    goGui.startGUI();
  }

  /**
   * When a game is started the logic is added and the board is created. Every game has a logic list
   * of players represented by the connection handlers on the server side
   */
  public GoGame(int dimension, List<ConnectionHandler> queuedPlayers) {
    board = new Board(dimension);
    logic = new GoLogic();
    players = queuedPlayers;
    players.getFirst().stone = Stone.BLACK;
    players.getLast().stone = Stone.WHITE;
    turn = players.getFirst();
    logic.dimension = dimension;
    consecutivePasses = 0;
    goGui = new GoGuiIntegrator(true, false, dimension);
    goGui.startGUI();
  }

  /**
   * If a move is valid, the position of the board is updated.
   *
   * @param move being played.
   */
  @Override
  public void updateState(Move move) {
    consecutivePasses = 0;
    // Define the new board position
    Position potentialPosition = new Position(board.currentPosition, move);
    List<int[]> capturedStones = logic.checkCaptures(potentialPosition, move.stone);
    int[] xy = logic.calculateXY(move.index);
    try {
      goGui.addStone(xy[0], xy[1], move.stone == Stone.WHITE);
      for (int[] capture : capturedStones) {
        goGui.removeStone(capture[0], capture[1]);
      }
    } catch (InvalidCoordinateException e) {
      System.out.println("Critical error");
      goGui.stopGUI();
    }
    board.currentPosition = potentialPosition;
    // Store the score of the new position
    Score score = logic.score(board.currentPosition);
    board.currentPosition.score = score;
    scores.put(scores.size(), score);
    updateScorePositionHashMap(score.toString());
  }

  @Override
  public Position getStatePosition() {
    return board.currentPosition;
  }

  @Override
  public String getStateString() {
    return getStatePosition().toString();
  }

  @Override
  public boolean validateMove(Move move) throws InvalidMoveException {
    // Check if the sender of the move is actually the one at play, if not return false
    return logic.validMove(scorePositionHashMap, board.currentPosition, move);
  }

  @Override
  public List<ConnectionHandler> getPLayers() {
    return players;
  }

  @Override
  public void switchTurn() {
    turn = getOtherPlayer(turn);
  }

  /**
   * Get the winner of the game. Score is based on area
   *
   * @return Which stone won,
   */
  @Override
  public Stone getWinner() {
    // returns the winner of the board connected to the game logic
    if (board.currentPosition.score.scoreBlack > board.currentPosition.score.scoreWhite) {
      return Stone.BLACK;
    } else if (board.currentPosition.score.scoreBlack < board.currentPosition.score.scoreWhite) {
      return Stone.WHITE;
    } else {
      return Stone.NONE;
    }
  }

  @Override
  public void setPosition(Position position) {
    board.currentPosition = position;
  }


  /**
   * This method is called when a pass is received from a player.
   *
   * @param player that played a pass
   */
  @Override
  public int pass(ConnectionHandler player) {
    consecutivePasses += 1;
    return consecutivePasses;
  }

  @Override
  public String gameOverScore() {
    return board.currentPosition.score.toString();
  }

  @Override
  public void gameOverResign(ConnectionHandler player) {

  }

  @Override
  public ConnectionHandler getOtherPlayer(ConnectionHandler player) {
    if (players.getFirst() == player) {
      return players.getLast();
    } else {
      return players.getFirst();
    }
  }

  @Override
  public ConnectionHandler getAtTurn() {
    return turn;
  }

  private void updateScorePositionHashMap(String score) {
    List<Integer> newHashes = new ArrayList<>();
    // get the list corresponding to the score
    List<Integer> hashes = scorePositionHashMap.get(score);
    if (hashes != null) {
      newHashes.addAll(hashes);
    }
    newHashes.add(board.currentPosition.hash);
    scorePositionHashMap.put(score, newHashes);
  }

  @Override
  public int getDimension() {
    return board.dimension;
  }
}
