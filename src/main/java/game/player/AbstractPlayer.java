package game.player;

import connectivity.client.PlayerConnection;

public abstract class AbstractPlayer implements Player{
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
}

