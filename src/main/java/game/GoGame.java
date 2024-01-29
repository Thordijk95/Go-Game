package game;

import connectivity.server.ConnectionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class the represents a GoGame, implements the Game interface.
 */
public class GoGame implements Game{
  private ConnectionHandler turn;
  private GoLogic logic = new GoLogic();
  public Board board;
  public HashMap<Stone, ConnectionHandler> players;
  // List of old positions
  private List<Position> previousPositions = new ArrayList<>();
  // Hashmap that links the ith positions to the score
  private HashMap<Integer, Score> scores = new HashMap<>();

  /**
   * When a game is started the logic is added and the board is created.
   * Every game has a logic list of players represented by the connection handlers on the server side
   */
  public GoGame(int dimension, List<ConnectionHandler> queuedPlayers){
    board = new Board(dimension);
    players = new HashMap<>();
    players.put(Stone.BLACK, queuedPlayers.get(0));
    players.put(Stone.WHITE, queuedPlayers.get(1));
    turn = players.get(Stone.BLACK);
    logic.dimension = dimension;
  }

  /**
   * If a move is valid, the position of the board is updated.
   * @param move being played.
   */
  @Override
  public void updateState(Move move) {
    // Update the history and the HashMap used for comparing positions
    previousPositions.add(board.currentPosition);
    // Define the new board position
    board.currentPosition = new Position(board.currentPosition, move);
    // Store the score of the new position
    Score score = logic.score(board.currentPosition);
    board.currentPosition.score = score;
    scores.put(scores.size(), score);
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
  public boolean validateMove(Move move, ConnectionHandler player) {
    // Check if the sender of the move is actually the one at play, if not return false
    if (player != turn) {
      return false;
    } else { // validate the move based on the logic of the game go
      return logic.validMove(previousPositions, board.currentPosition, move);
    }

  }


  @Override
  public HashMap<Stone, ConnectionHandler> getPLayers() {
    return players;
  }
  @Override
  public void switchTurn() {
    turn = (players.get(Stone.BLACK) == turn) ? players.get(Stone.WHITE) : players.get(Stone.BLACK);
  }

  /**
   * Get the winner of the game.
   * Score is based on area
   * @return Which stone won,
   */
  public Stone getWinner() {
    // returns the winner of the board connected to the game logic
    if (board.currentPosition.score.scoreBlack > board.currentPosition.score.scoreWhite) {
      return Stone.BLACK;
    } else if (board.currentPosition.score.scoreBlack < board.currentPosition.score.scoreWhite) {
      return Stone.WHITE;
    } else { return Stone.NONE; }
  }
  @Override
  public void setPosition(Position position) {
    board.currentPosition = position;
  }

}
