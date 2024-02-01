package connectivity.client;

import connectivity.protocol.GoProtocol;
import connectivity.SocketConnection;
import game.Move;
import game.Stone;
import game.player.AIPlayer;
import game.player.Player;
import java.io.IOException;
import java.net.InetAddress;

public class PlayerConnection extends SocketConnection {

  public Player player;

  /**
   * Make a new TCP connection to the given host and port. The receiving thread is not started yet.
   * Call start on the returned SocketConnection to start receiving messages.
   *
   * @param host the address of the server to connect to
   * @param port the port of the server to connect to
   * @throws IOException if the connection cannot be made or there was some other I/O problem
   */
  public PlayerConnection(InetAddress host, int port) throws IOException {
    super(host, port);
  }

  /**
   * Handles a message received from the connection.
   *
   * @param message the message received from the connection
   */
  @Override
  public void handleMessage(String message) {
    System.out.println("[CLIENT " + player.getUsername() + "] " + message);
    String[] splitString = message.split("~");

    switch (splitString[0]) {
      case GoProtocol.LOGIN -> // Do nothing, this should never come from the server
          sendMessage(GoProtocol.ERROR + "~This should not come from the server");
      case GoProtocol.QUEUE ->
          sendMessage(GoProtocol.ERROR + "~This should not come from the server");
      case GoProtocol.PASS -> {
        // Opponent passed, make a move
        if (!splitString[1].equals(player.getColor())) {
          player.determineMove();
        }
      }
      case GoProtocol.ACCEPTED -> {
        player.setLoggedIn();
        if (player instanceof AIPlayer) {
          player.automatedQueue();
        }
      }
      case GoProtocol.REJECTED -> player.handleReject();
      case GoProtocol.QUEUED -> player.setQueued();
      case GoProtocol.MAKE_MOVE -> player.determineMove();
      case GoProtocol.MOVE -> {
        // Update your board with any move reflected by the server
        int index = Integer.parseInt(splitString[1]);
        Stone stone = splitString[2].equalsIgnoreCase("black") ? Stone.BLACK : Stone.WHITE;
        player.updateState(new Move(stone, index));
      }
      case GoProtocol.HELLO -> { // Acknowledge connection
        player.setConnected();
        if (player instanceof AIPlayer) {
          player.automatedLogin();
        }
      }
      case GoProtocol.GAME_STARTED -> {
        String[] names = splitString[1].split(",");
        String username1 = names[0];
        player.setQueued();
        int boardDimension = Integer.parseInt(splitString[2]);
        player.initializeState(boardDimension);
        player.setInGame();
        if (player.getUsername().equals(username1)) {
          player.setColor("black");
        } else {
          player.setColor("white");
        }
      }
      case GoProtocol.GAME_OVER -> {
        System.out.println(message); // Game over TODO fix
        player.gameOver();
      }
      case GoProtocol.ERROR -> player.handleError();
    }
  }

  /**
   * Handles a disconnect from the connection, i.e., when the connection is closed.
   */
  @Override
  protected void handleDisconnect() {

  }

  @Override
  public boolean sendMessage(String message) {
    return super.sendMessage(message);
  }

  public void sendMove(Move move) {
    super.sendMessage(
        String.format(GoProtocol.MOVE + "~" + move.index + "~" + move.stone.toString()));
  }
}
