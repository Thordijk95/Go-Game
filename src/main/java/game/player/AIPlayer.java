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
    logic.dimension = goGame.getDimension();
    // int index = naiveAI();
    int index = notSoNaiveAI();
    Move move = new Move(stone, index);
    playerConnection.sendMove(move);
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
        playerConnection = new PlayerConnection(InetAddress.getByName(args[0]),
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

  @Override
  public void handleReject(String rejectedName) {
    int iterator;
    String baseName;
    if (rejectedName.contains("_")) {
      String[] name_config = rejectedName.split("_");
      baseName = name_config[0];
      iterator = Integer.parseInt(name_config[1]);
      iterator += 1;
    } else {
      iterator = 1;
    }
    String newName = rejectedName+"_"+ iterator;
    username = newName;
    playerConnection.sendMessage("LOGIN~"+username);
  }

  private int naiveAI() {
    int attempt = 0;
    Random rand = new Random();
    while (true) {
      int guess = rand.nextInt((int) Math.pow(goGame.getDimension(), 2));
      Move move = new Move(stone, guess);
      try {
        if (goGame.validateMove(move)) {
          return guess;
        }
      } catch (InvalidMoveException e) {
        // do nothing, try 20 times
        attempt += 1;
        if (attempt == 20) {
          return -1;
        }
      }
    }
  }
  private int notSoNaiveAI() {
    double temperature = 20;
    Random rand = new Random();
    Score startScore = goGame.getStatePosition().score;

    int potentialIntersection = logic.calculateIndex(logic.calculateXY(
        rand.nextInt((int) Math.pow(goGame.getDimension(), 2))));
    int bestPotentialIntersection = potentialIntersection;
    Position potentialPosition = new Position(goGame.getStatePosition(),
        new Move(getStone(), potentialIntersection));
    Position bestPosition = potentialPosition.clonePosition(goGame.getDimension());
    HashMap<Stone, List<Cluster>> potentialClusters = logic.findClusters(potentialPosition);
    List<int[]> captures = logic.checkCaptures(potentialPosition, stone);
    // if any capture was found, perform this move
    if (!captures.isEmpty()) {
      // do the capture
      return bestPotentialIntersection;
    } else {
      potentialIntersection = logic.calculateIndex(logic.calculateXY(
          rand.nextInt((int) Math.pow(goGame.getDimension(), 2))));
      potentialPosition = new Position(goGame.getStatePosition(),
          new Move(getStone(), potentialIntersection));
      HashMap<Stone, List<Cluster>> clusters = logic.findClusters(potentialPosition);
      if (clusters.get(stone).size() <= 1) {
        return bestPotentialIntersection;
      }
      while (temperature >= 1) {
        potentialIntersection = logic.calculateIndex(logic.calculateXY(
            rand.nextInt((int) Math.pow(goGame.getDimension(), 2))));
        potentialPosition = new Position(goGame.getStatePosition(),
            new Move(getStone(), potentialIntersection));
        logic.findClusters(potentialPosition);
        Score score = logic.score(potentialPosition);
        switch (stone) {
          case BLACK -> {
//            List<Cluster> clusters = potentialClusters.get(stone);
            // Check if you infringe on territory
            if (score.scoreWhite < startScore.scoreWhite) {
              temperature = temperature * 0.98;
              if (score.scoreWhite < bestPosition.score.scoreWhite) {
                bestPosition = potentialPosition.clonePosition(goGame.getDimension());
                bestPotentialIntersection = potentialIntersection;
              }
            }
          }
          case WHITE -> {
            if (score.scoreBlack < startScore.scoreBlack) {
              temperature = temperature * 0.98;
              if (score.scoreBlack < bestPosition.score.scoreBlack) {
                bestPosition = potentialPosition.clonePosition(goGame.getDimension());
                bestPotentialIntersection = potentialIntersection;
              }
            }
          }
        }
      }
    }
    return bestPotentialIntersection;
  }
}
