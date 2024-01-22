package Connectivity.Server;

import Connectivity.SocketConnection;
import Game.Player.AbstractPlayer;
import Game.Player.Player;

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

  public void receiveMove(String move) {

  }

}
