package Game.Player;

public interface Player {
  String getUsername();
  void setUsername(String username);
  void sendMessage(String message);
  void sendMove(String message);

}
