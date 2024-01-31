package game.player;

import connectivity.SocketConnection;
import connectivity.client.PlayerConnection;
import game.Board;
import game.Game;
import game.GoGame;
import game.Move;
import game.Position;
import game.Stone;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class AbstractPlayer implements Player{
  public boolean queued = false;
  private boolean connected = false;
  private boolean logggedIn = false;
  private boolean inGame = false;
  public PlayerConnection playerConnection;
  public String username;
  public Stone stone;

  public Position position;

  public Game goGame;
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
  public boolean getConnected() {
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
  public abstract void determineMove();

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
    goGame = new GoGame(boardDimensions, this);
  }

  @Override
  public void updateState(Move move) {
    goGame.updateState(move);
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
  public void setLoggedIn() {
    logggedIn = !logggedIn;
  }

  @Override
  public boolean getLoggedIn() {
    return logggedIn;
  }

  @Override
  public void handleReject() {

  }

  @Override
  public void handleError() {
    // do nothing
  }

  @Override
  public void gameOver() {

  }
  @Override
  public void setInGame() {
    inGame = !inGame;
  }


  @Override
  public boolean getInGame() {
    return inGame;
  }

  @Override
  public abstract void automatedLogin();

  @Override
  public abstract void automatedQueue();
}

