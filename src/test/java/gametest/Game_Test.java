package gametest;

import connectivity.SocketServer;
import connectivity.server.GoServer;
import game.player.Player;
import game.player.PlayerPlayer;
import java.io.IOException;
import java.net.InetAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Game_Test {

  private SocketServer goServer;

  @BeforeEach
  void setup() throws IOException {
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
  void startGameThroughServer() {
    new Thread(this::acceptConnections).start();

    Player player1 = new PlayerPlayer("Henk");
    Player player2 = new PlayerPlayer("Henk2");

    try {
      player1.setPlayerConnection(InetAddress.getLocalHost(), 8080);
      player2.setPlayerConnection(InetAddress.getLocalHost(), 8080);
    } catch (IOException e) {
      e.printStackTrace();
    }

    while(!(player1.getConnected() && player2.getConnected())) {
      System.out.println("wait for connection");
    }

    player1.sendMessage("LOGIN~Henk");
    player2.sendMessage("LOGIN~Henk2");

    while(!(player1.getLoggedIn() && player2.getLoggedIn())) {
      System.out.println("Wait for login");
    }

    player1.sendMessage("QUEUE");
    player2.sendMessage("QUEUE");

    while (!(player1.getQueued() && player2.getQueued()) ) {
      System.out.println("Wait for queued response");
    }
  }
}
