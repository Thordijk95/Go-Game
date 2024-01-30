package game.player;

import connectivity.SocketConnection;
import connectivity.client.PlayerConnection;
import connectivity.server.ConnectionHandler;
import game.Move;
import java.io.IOException;
import java.net.InetAddress;

public interface Player {
  String getUsername();
  void setPlayerConnection(InetAddress inetAddress, int port) throws IOException;
  PlayerConnection getPlayerConnection();
  void setUsername(String username);
  void sendMessage(String message);
  void sendMove(String message);

  boolean isConnected();

  void setConnected();
  void setDisconnected();

  Move determineMove();

  void handleReject();

  String getColor();
  void setColor(String color);

  void updateState(Move move);

  void initializeState(int boardDimensions);

  void setQueued();
  boolean getQueued();

}
