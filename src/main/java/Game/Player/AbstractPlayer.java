package Game.Player;

public class AbstractPlayer implements Player{
  private String username;

  public AbstractPlayer(String username) {
    this.username = username;
  }

  @Override
  public String getUsername() {
    return username;
  }
}

