package game;

import com.nedap.go.exceptions.InvalidMoveException;
import com.nedap.go.exceptions.NoTurnAssignedException;
import connectivity.server.ConnectionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public interface Game{

  List<ConnectionHandler> players = new ArrayList<>();

  String getStateString();
  Position getStatePosition();
  boolean validateMove(Move move, ConnectionHandler player) throws InvalidMoveException;

  void updateState(Move move);

  List<ConnectionHandler> getPLayers();
  ConnectionHandler getOtherPlayer(ConnectionHandler player);
  void switchTurn();

  void setPosition(Position position);

  void pass(ConnectionHandler player);

  /**
   * Game over is called when two consecutive passes are played, or when a player Resigns.
   */
  void gameOverScore();
  void gameOverResign(ConnectionHandler player);

  ConnectionHandler getAtTurn() throws NoTurnAssignedException;

  int getDimension();
}
