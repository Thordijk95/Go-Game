package game.player;

import java.io.IOException;
import java.net.InetAddress;

public class AIPlayer extends AbstractPlayer{
  private static final String username = "AI";

  public AIPlayer(String username) {
    super(username);
  }

  @Override
  public void setPlayerConnection(InetAddress inetAddress, int port) throws IOException {

  }

  @Override
  public void sendMessage(String message) {

  }

  @Override
  public void sendMove(String message) {

  }
}
