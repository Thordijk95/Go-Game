package Game.Player;

import Connectivity.Client.PlayerConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class PlayerPlayer extends Game.Player.AbstractPlayer {
  private PlayerConnection playerConnection;
  private String username;

  public PlayerPlayer() { }

  public PlayerPlayer(String username) {
    this.username = username;
  }

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
