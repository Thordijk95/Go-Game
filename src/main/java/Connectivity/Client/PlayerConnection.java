package Connectivity.Client;

import Connectivity.Protocol.GoProtocol;
import Connectivity.SocketConnection;
import Game.Player.Player;
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
      case GoProtocol.LOGIN -> {
        sendMessage(message);
        player.setUsername(splitString[1]);
      }
      case GoProtocol.QUEUE -> sendMessage(message);
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
}
