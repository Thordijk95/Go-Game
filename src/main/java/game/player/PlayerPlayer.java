package game.player;

import connectivity.client.PlayerConnection;
import connectivity.client.PlayerTui;
import game.Board;
import game.Move;
import game.Stone;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class PlayerPlayer extends game.player.AbstractPlayer {

  public PlayerPlayer() { }

  public PlayerPlayer(String username) {
    super.setUsername(username);
  }

  public PlayerTui playerTui;

  @Override
  public void sendMessage(String message) {
    playerConnection.sendMessage(message);
  }

  @Override
  public void sendMove(String message) {

  }

  @Override
  public void setPlayerConnection(InetAddress inetAddress, int port) throws IOException {
    try {
      playerConnection = new PlayerConnection(inetAddress, port);
      playerConnection.player = this;
      playerConnection.start();
    } catch (UnknownHostException e){
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
  public Move determineMove() {
    if (playerTui != null) {
      return playerTui.determinMoveTui();
    } else {
      return new Move(stone, new Random().nextInt(playerBoard.dimension*playerBoard.dimension));
    }
  }

  @Override
  public void handleReject() {

  }
}
