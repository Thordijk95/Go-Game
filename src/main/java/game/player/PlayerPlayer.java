package game.player;

import connectivity.client.PlayerConnection;
import game.Move;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PlayerPlayer extends game.player.AbstractPlayer {
  private PlayerConnection playerConnection;

  public PlayerPlayer() { }

  public PlayerPlayer(String username) {
    super.setUsername(username);
  }

  @Override
  public void sendMessage(String message) {
    playerConnection.sendMessage(message);
  }

  @Override
  public void sendMove(String message) {

  }

  @Override
  public void setPlayerConnection(InetAddress inetAddress, int port) throws IOException {
    try {
      playerConnection = new PlayerConnection(inetAddress, port);
      playerConnection.player = this;
      playerConnection.start();
    } catch (UnknownHostException e){
      e.printStackTrace();
    }
  }

  @Override
  public String getUsername() {
    return super.getUsername();
  }

  @Override
  public void setUsername(String username) {
    super.setUsername(username);
  }

  @Override
  public Move determineMove() {
    return null;
  }

  @Override
  public void handleReject() {

  }
}
