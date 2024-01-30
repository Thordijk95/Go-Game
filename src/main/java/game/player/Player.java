package game.player;

import java.io.IOException;
import java.net.InetAddress;

public interface Player {
  String getUsername();
  void setPlayerConnection(InetAddress inetAddress, int port) throws IOException;
  void setUsername(String username);
  void sendMessage(String message);
  void sendMove(String message);

  boolean isConnected();

  void setConnected();
  void setDisconnected();

}
