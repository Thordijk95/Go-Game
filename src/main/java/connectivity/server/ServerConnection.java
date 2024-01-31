package connectivity.server;

import com.nedap.go.exceptions.InvalidMessageException;
import com.nedap.go.exceptions.InvalidNameException;
import connectivity.SocketConnection;
import connectivity.protocol.GoProtocol;

import game.GoLogic;
import game.Move;
import game.Stone;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ServerConnection extends SocketConnection {

  public ConnectionHandler connectionHandler;
  public String username;

  /**
   * Create a new SocketConnection. This is not meant to be used directly.
   * Instead, the SocketServer and SocketClient classes should be used.
   *
   * @param socket the socket for this connection
   * @throws IOException if there is an I/O exception while initializing the Reader/Writer objects
   */
  protected ServerConnection(Socket socket) throws IOException {
    super(socket);
  }

  /**
   * Make a new TCP connection to the given host and port.
   * The receiving thread is not started yet. Call start on the returned SocketConnection to start receiving messages.
   *
   * @param host the address of the server to connect to
   * @param port the port of the server to connect to
   * @throws IOException if the connection cannot be made or there was some other I/O problem
   */
  protected ServerConnection(InetAddress host, int port) throws IOException {
    super(host, port);
  }

  /**
   * Make a new TCP connection to the given host and port.
   * The receiving thread is not started yet. Call start on the returned SocketConnection to start
   * receiving messages.
   * @param host the address of the server to connect to
   * @param port the port of the server to connect to
   * @throws IOException if the connection cannot be made or there was some other I/O problem
   */
  protected ServerConnection(String host, int port) throws IOException {
    super(host, port);
  }

  /**
   * Handles a message received from the connection.
   * Message is parsed based on the provided protocol.
   * @param message the message received from the connection
   */
  @Override
  protected void handleMessage(String message) {
    System.out.println("[SERVER] " + message);
    String[] substrings = message.split(GoProtocol.SEPARATOR);
    try {
      switch (substrings[0]) {
        // Message is coming from the chat server and was sent by another client,
        // print it to the screen of the current client
        case GoProtocol.LOGIN -> {
          username = substrings[1];
          connectionHandler.receiveUsername(substrings[1]);
        }
        case GoProtocol.QUEUE -> {
          connectionHandler.receiveQueueRequest();
        }
        case GoProtocol.MOVE -> {
          int index;
          String location = substrings[1];
          if (location.contains(",")) {
            String[] coordinates = location.split(",");
            int column = Integer.parseInt(coordinates[0]);
            int row = Integer.parseInt(coordinates[1]);
            index = new GoLogic().calculateIndex(new int[]{column, row});
          } else {
            index = Integer.parseInt(location);
          }
          Move move = new Move(connectionHandler.stone, index);
          connectionHandler.receiveMove(move);
        }
        case GoProtocol.PASS -> connectionHandler.receivePass();
        case GoProtocol.RESIGN -> connectionHandler.receiveResign();
        case GoProtocol.ERROR -> {
          System.out.println("[SERVER]");
          System.out.println(substrings[1]);
        }
        default -> {
          System.out.println(message);
          throw new InvalidMessageException(
              "Message does not adhere to the protocol."); // invalid entry, do nothing
        }
      }
    } catch (InvalidMessageException e) {
      e.printStackTrace();
      sendMessage(GoProtocol.ERROR + "~Invalid message format");
    }
  } //else do nothing


  /**
   * Handles a disconnect from the connection, i.e., when the connection is closed.
   */
  @Override
  protected void handleDisconnect() {
    if (username == null) {
      username = "no-name";
    } else {
      connectionHandler.goServer.removePlayer(connectionHandler);
    }
    System.out.println("Disconnect " + username);
  }

  /**
   * Send a move to the players for them to update their board with.
   * @param protocol used to differentiate different messages
   * @param move that was played.
   */
  public void sendMove(Move move) {
    super.sendMessage(String.format(GoProtocol.MOVE + "~" + move.index + "~" + move.stone.toString()));
  }

  @Override
  public boolean sendMessage(String message) {
    String msg = String.format(message);
    return super.sendMessage(msg);
  }

  public String getUsername() {return username;}

}
