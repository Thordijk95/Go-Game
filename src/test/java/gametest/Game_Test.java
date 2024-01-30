package gametest;

import com.nedap.go.Go;
import connectivity.SocketServer;
import connectivity.client.PlayerConnection;
import connectivity.server.ConnectionHandler;
import connectivity.server.GoServer;
import game.Game;
import game.player.Player;
import game.player.PlayerPlayer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

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

    while(true) {
      if (player1.isConnected() && player2.isConnected()){
        break;
      }
    }

    player1.sendMessage("LOGIN~Henk");
    player2.sendMessage("LOGIN~Henk2");

    player1.sendMessage("QUEUE");
    player2.sendMessage("QUEUE");

    while (true) {
      if (player1.getQueued() && player2.getQueued()) {
        break;
      }
    }
  }
}
