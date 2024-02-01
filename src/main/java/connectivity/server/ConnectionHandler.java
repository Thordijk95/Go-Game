package connectivity.server;

import connectivity.protocol.GoProtocol;
import game.Move;
import game.Stone;

public class ConnectionHandler {

  public ServerConnection serverConnection;
  public GoServer goServer;
  public Stone stone;
  public String username;
  private boolean inGame;
  private boolean inQueue;
  private boolean loggedIn;

  public ConnectionHandler() {
  }

  public void receiveQueueRequest() {
    goServer.addToQueue(this);
  }

  /**
   * Get the username of the player connected through this handler and return it.
   * @return the player name
   */
  public String getUsername() {
    return serverConnection.getUsername();
  }

  public void receiveUsername(String name) {
    this.username = name;
    goServer.addPlayer(this);
  }

  public void receiveMove(Move move) {
    goServer.receiveMove(move, this);
  }

  public void receivePass() {
    goServer.receivePass(this);
  }

  public void receiveResign() {
    goServer.receiveResign(this);
  }

  public void sendMove(Move move) {
    serverConnection.sendMove(move);
  }

  public void sendMessage(String message) {
    serverConnection.sendMessage(message);
  }

  public void sendError(String errorMessage) {
    serverConnection.sendMessage(GoProtocol.ERROR + "~" + errorMessage);
  }

  /**
   * Called by the server when a move is invalid.
   * @param message to send to the player.
   */
  public void sendMoveRefused(String message) {
    serverConnection.sendMessage(GoProtocol.ERROR + "~" + message);
    serverConnection.sendMessage(GoProtocol.MAKE_MOVE);
  }

  public void setInGame() {
    inGame = !inGame;
  }

  public boolean getInGame() {
    return inGame;
  }

  public void setInQueue() {
    inQueue = !inQueue;
  }

  public boolean getInQueue() {
    return inQueue;
  }

  public void setLoggedIn() {
    loggedIn = !loggedIn;
  }

  public boolean getLoggedIn() {
    return loggedIn;
  }


}
