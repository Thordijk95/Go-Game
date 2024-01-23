package Connectivity.Server;

import Game.Move;

public class ConnectionHandler {
  public ServerConnection serverConnection;
  public GoServer goServer;
  public ConnectionHandler() {}

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

  public void receiveUsername(String username) {
    goServer.addPlayer(this);
  }

  public void receiveMove(Move move) {
    goServer.receiveMove(move, this);

  }

  public void sendMove(String move) {
    serverConnection.sendMove(move);
  }

}
