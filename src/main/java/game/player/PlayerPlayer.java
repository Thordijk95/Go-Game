package game.player;

import connectivity.client.PlayerConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PlayerPlayer extends game.player.AbstractPlayer {
  private PlayerConnection playerConnection;
  private String username;

  public PlayerPlayer() { }

  public PlayerPlayer(String username) {
    this.username = username;
  }

  @Override
  public void setPlayerConnection(InetAddress inetAddress, int port) throws IOException {
    try {
      playerConnection = new PlayerConnection(InetAddress.getLocalHost(), port);
      playerConnection.player = this;
    } catch (UnknownHostException e){
      e.printStackTrace();
    }
  }
  @Override
  public void sendMessage(String message) {
    playerConnection.sendMessage(message);
  }

  @Override
  public void sendMove(String message) {

  }
}
