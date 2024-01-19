package Connectivity.Server;

import Connectivity.SocketConnection;

public class PlayerHandler {
  SocketConnection playerConection;
  public PlayerHandler() {
    playerConection = new PlayerConnection();
  }

}
