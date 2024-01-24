package Game;

import Connectivity.Server.ConnectionHandler;
import Game.Player.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.geometry.Pos;

public interface Game{
  List<ConnectionHandler> players = new ArrayList<>();

  String getStateString();
  Position getStatePosition();
  boolean validateMove(Move move, ConnectionHandler player);

  void updateState(Move move);

  HashMap<Stone, ConnectionHandler> getPLayers();
  void switchTurn();

  void setPosition(Position position);

}
