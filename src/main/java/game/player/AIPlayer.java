package game.player;

import com.nedap.go.exceptions.InvalidMoveException;
import connectivity.client.PlayerConnection;
import connectivity.protocol.GoProtocol;
import game.Move;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AIPlayer extends AbstractPlayer {

  private PlayerConnection playerConnection;
  private static final String USERNAME = "AI";

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
  public void determineMove() {
    int attempt = 0;
    Random rand = new Random();
    while (true) {
      int guess = rand.nextInt((int) Math.pow(goGame.getDimension(), 2));
      Move move = new Move(stone, guess);
      try {
        if (goGame.validateMove(move)) {
          playerConnection.sendMove(move);
          break;
        }
      } catch (InvalidMoveException e) {
        // do nothing, try 20 times
        attempt += 1;
        if (attempt == 20) {
          playerConnection.sendMessage(GoProtocol.PASS);
          break;
        }
      }
    }
  }

  @Override
  public void automatedLogin() {
    playerConnection.sendMessage(GoProtocol.LOGIN + "~" + USERNAME);
  }

  @Override
  public void automatedQueue() {
    playerConnection.sendMessage(GoProtocol.QUEUE);
  }

  public static void main(String[] args) {
    AIPlayer aiPlayer = new AIPlayer(USERNAME);
    aiPlayer.run(args);
  }

  public void run(String[] args) {
    int attempt = 0;
    while (true) {
      try {
        playerConnection = new PlayerConnection(InetAddress.getLocalHost(),
            Integer.parseInt(args[1]));
        playerConnection.player = this;
        break;
      } catch (IOException e) {
        attempt += 1;
        if (attempt == 10000) {
          break;
        }
      }
    }
    playerConnection.start();
    while (!super.getConnected()) {
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
