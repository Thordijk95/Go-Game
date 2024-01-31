package connectivitytest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import connectivity.SocketServer;
import connectivity.protocol.GoProtocol;
import connectivity.server.GoServer;
import game.player.Player;
import game.player.PlayerPlayer;
import java.io.IOException;
import java.net.InetAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class connectivityTest {

  private GoServer goServer;

  @BeforeEach
  void setup() throws IOException {
    // Start the server and open the connection on a port
    goServer = new GoServer(8080);
  }

  private void acceptConnections() {
    try {
      goServer.acceptConnections();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  @Test
  void connectSinglePlayer() throws IOException{
    assertTrue(goServer.getPort() > 0);
    assertTrue(goServer.getPort() <= 65535);
    // Start the server
    new Thread(this::acceptConnections).start();

    Player playerPlayer = new PlayerPlayer();
    playerPlayer.setUsername("Thomas");
    assertEquals("Thomas", playerPlayer.getUsername());

    playerPlayer.setPlayerConnection(InetAddress.getLocalHost(), 8080);
    String message = String.format(GoProtocol.LOGIN + "~" + playerPlayer.getUsername());
    System.out.println("Sending message once:" + message);
    playerPlayer.sendMessage(message);
    // Wait for the server to process the login
    while(!playerPlayer.getConnected()) {
      //do nothing
    }
    // check that the player is properly added to the server
    assertEquals(playerPlayer.getUsername(), goServer.getPlayerConnectionHandles().getFirst().getUsername());

    goServer.close();
  }

  @Test
  void connectTwoPlayersWithSameName() throws IOException {
    new Thread(this::acceptConnections).start();
    Player player1 = new PlayerPlayer("Henk");
    Player player2 = new PlayerPlayer("Henk");

    player1.setPlayerConnection(InetAddress.getLocalHost(), 8080);
    player2.setPlayerConnection(InetAddress.getLocalHost(), 8080);

    // Login with both players
    String message = GoProtocol.LOGIN + "~" + player1.getUsername();
    player1.sendMessage(message);
    player2.sendMessage(message);

    while(true) {
      if (player1.getConnected()) break;
      if (player2.getConnected()) break;
    }
    assertEquals(1, goServer.getPlayerConnectionHandles().size());

    player2.setUsername("Henk2");
    String message2 = GoProtocol.LOGIN + "~" + player2.getUsername();

    player2.sendMessage(message2);

    while(true) {
      if (player2.getConnected()) break;
    }

    assertEquals(2, goServer.getPlayerConnectionHandles().size());
    goServer.close();
  }
}
