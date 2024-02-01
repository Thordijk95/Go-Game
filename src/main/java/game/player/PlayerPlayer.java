package game.player;

import connectivity.client.PlayerConnection;
import connectivity.client.PlayerTui;
import game.Move;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class PlayerPlayer extends AbstractPlayer {

  public PlayerPlayer() {
  }

  public PlayerPlayer(String username) {
    super.setUsername(username);
  }

  public PlayerTui playerTui;

  @Override
  public void sendMessage(String message) {
    playerConnection.sendMessage(message);
  }

  @Override
  public void setPlayerConnection(InetAddress inetAddress, int port) throws IOException {
    try {
      playerConnection = new PlayerConnection(inetAddress, port);
      playerConnection.player = this;
      playerConnection.start();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getUsername() {
    return super.getUsername();
  }

  @Override
  public void setUsername(String username) {
    super.setUsername(username);
  }

  @Override
  public void determineMove() {
    if (playerTui != null) {
      playerTui.determineMoveTui();
    } else {
      playerConnection.sendMove(
          new Move(stone, new Random().nextInt((int) Math.pow(super.goGame.getDimension(), 2))));
    }
  }

  @Override
  public String getStoneString() {
    return null;
  }

  @Override
  public void automatedLogin() {

  }

  @Override
  public void automatedQueue() {

  }
}
