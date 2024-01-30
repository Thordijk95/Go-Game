package connectivity.server;

import connectivity.protocol.GoProtocol;
import game.Move;
import game.Stone;

public class ConnectionHandler {
  public ServerConnection serverConnection;
  public GoServer goServer;
  public Stone stone;
  public String username;

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
    System.out.println("receiveUsername ConnectionHandler");
    this.username = username;
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
  public void sendMove(String move, Stone stone) {
    serverConnection.sendMove(GoProtocol.MOVE, move, stone);
  }

  public void sendMessage(String message) {
    serverConnection.sendMessage(message);
  }


}
