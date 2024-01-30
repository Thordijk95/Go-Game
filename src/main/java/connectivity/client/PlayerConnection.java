package connectivity.client;

import com.nedap.go.Go;
import connectivity.protocol.GoProtocol;
import connectivity.SocketConnection;
import game.player.Player;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class PlayerConnection extends SocketConnection {
  public Player player;

  /**
   * Create a new SocketConnection. This is not meant to be used directly. Instead, the SocketServer
   * and SocketClient classes should be used.
   *
   * @param socket the socket for this connection
   * @throws IOException if there is an I/O exception while initializing the Reader/Writer objects
   */
  protected PlayerConnection(Socket socket) throws IOException {
    super(socket);
  }

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
   * Make a new TCP connection to the given host and port. The receiving thread is not started yet.
   * Call start on the returned SocketConnection to start receiving messages.
   *
   * @param host the address of the server to connect to
   * @param port the port of the server to connect to
   * @throws IOException if the connection cannot be made or there was some other I/O problem
   */
  protected PlayerConnection(String host, int port) throws IOException {
    super(host, port);
  }

  /**
   * Handles a message received from the connection.
   *
   * @param message the message received from the connection
   */
  @Override
  public void handleMessage(String message) {
    String[] splitString = message.replace(" ", "").split("~");

    switch (splitString[0]) {
      case GoProtocol.LOGIN -> {// Do nothing, this should never come from the server
        sendMessage(GoProtocol.ERROR + "~This should not come from the server");
      }
      case GoProtocol.QUEUE -> sendMessage(GoProtocol.ERROR + "~This should not come from the server");
      case GoProtocol.ACCEPTED -> {
        player.setConnected();
        System.out.println(message);
      }
      case GoProtocol.REJECTED -> {
        System.out.println(message);
        player.handleReject();
      }
      case GoProtocol.QUEUED -> System.out.println(message);
      case GoProtocol.MAKE_MOVE -> {
        if (splitString[1].equals(player.getUsername())) {
          player.determineMove();
        }
      }
      case GoProtocol.MOVE -> {

      }
      case GoProtocol.HELLO -> System.out.println(message);   // Acknowledge connection
      case GoProtocol.GAME_STARTED -> System.out.println(message); // Game started between two players
      case GoProtocol.GAME_OVER -> System.out.println(message); // Game over TODO fix
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
    System.out.println("Playerconnection sendMessage" + message);
    return super.sendMessage(message);
  }
}
