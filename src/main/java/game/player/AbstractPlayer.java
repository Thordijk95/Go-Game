package game.player;

import connectivity.client.PlayerConnection;
import game.Move;

public abstract class AbstractPlayer implements Player{
  private boolean connected = false;
  private PlayerConnection playerConnection;
  private String username;

  public AbstractPlayer() {};

  public AbstractPlayer(String username) {
    this.username = username;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public void setUsername(String username) {
    this.username = username;
  }
  @Override
  public abstract void sendMessage(String message);

  @Override
  public abstract void sendMove(String message);

  @Override
  public boolean isConnected() {
    return connected;
  }

  @Override
  public void setConnected() {
    connected = true;
  }

  @Override
  public void setDisconnected() {
    connected = false;
  }

  @Override
  public abstract Move determineMove();
}

