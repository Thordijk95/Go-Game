package game.player;

import connectivity.client.PlayerConnection;
import game.Board;
import game.Move;
import game.Position;
import game.Stone;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class AbstractPlayer implements Player{
  public boolean queued = false;
  private boolean connected = false;
  private PlayerConnection playerConnection;
  private String username;
  private Stone stone;

  private Position position;

  private Board playerBoard;

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

  @Override
  public String getColor() {
    return stone.toString();
  }

  @Override
  public void setColor(String color) {
    switch (color.toLowerCase()) {
      case "black" -> stone = Stone.BLACK;
      case "white" -> stone = Stone.WHITE;
    }
  }

  @Override
  public void initializeState(int boardDimensions) {
    playerBoard = new Board(boardDimensions);
  }

  @Override
  public void updateState(Move move) {
    position = new Position(position, move);
  }

  @Override
  public void setQueued() {
    queued = !(queued);
  }

  @Override
  public boolean getQueued() {
    return queued;
  }

  @Override
  public abstract void setPlayerConnection(InetAddress inetAddress, int port) throws IOException;

  @Override
  public PlayerConnection getPlayerConnection() {
    return playerConnection;
  }
}

