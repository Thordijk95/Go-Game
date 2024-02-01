package game.player;

import com.nedap.go.exceptions.InvalidMoveException;
import connectivity.client.PlayerConnection;
import connectivity.protocol.GoProtocol;
import game.Cluster;
import game.GoLogic;
import game.Move;
import game.Position;
import game.Score;
import game.Stone;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AIPlayer extends AbstractPlayer {

  private GoLogic logic;

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
    logic = new GoLogic();
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

  private int[] notSoNaiveAI() {
    double temperature = 100;
    Random rand = new Random();
    Score startScore = goGame.getStatePosition().score;

    int[] potentialIntersection = logic.calculateXY(rand.nextInt((int) Math.pow(goGame.getDimension(), 2)));
    Position potentialPosition = new Position(goGame.getStatePosition(),
        new Move(getStone(), logic.calculateIndex(potentialIntersection)));
    logic.findClusters(potentialPosition);
    List<int[]> captures = logic.checkCaptures(potentialPosition, stone);
    // if any capture was found, perform this move
    if (!captures.isEmpty()) {
      // do the capture
      return potentialIntersection;
    } else {
      for (int[] capture : captures) {
        potentialPosition.setIntersection(logic.calculateIndex(capture), Stone.NONE);
      }
      Score score = logic.score(potentialPosition);
      while (temperature >= 1) {
        switch (stone) {
          case BLACK -> {
            if (potentialPosition.score.scoreBlack > startScore.scoreBlack) {

            }
          }
        }

      }
    }
    return new int[] {0,0};
  }
}
