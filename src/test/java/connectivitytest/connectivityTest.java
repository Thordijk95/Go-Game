package connectivitytest;

import connectivity.SocketServer;
import connectivity.protocol.GoProtocol;
import connectivity.server.GoServer;
import game.player.Player;
import game.player.PlayerPlayer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class connectivityTest {

  SocketServer goServer;

  @BeforeEach
  void setup() {
    // Start the server and open the connection on a port
    try {
      goServer = new GoServer(InetAddress.getLocalHost(), 8080);
    } catch (IOException e) {
      System.out.println();
    }
  }

  @Test
  void connectSinglePlayer() {
    Player player = new PlayerPlayer();
    player.setUsername("Thomas");
    try {
      player.setPlayerConnection(InetAddress.getLocalHost(), 8080);
    } catch (IOException e) {
      System.out.println("Host does not exist");
      e.printStackTrace();
    }
    player.sendMessage(String.format(GoProtocol.LOGIN + "~" + player.getUsername()));

  }

}
