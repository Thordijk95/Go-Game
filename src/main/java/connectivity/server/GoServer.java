package connectivity.server;

import connectivity.SocketServer;
import game.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GoServer extends SocketServer {
  private List<ConnectionHandler> connectionHandlerList;
  private List<ConnectionHandler> queue;
  private List<Game> games;
  public Integer gameDimension;

  /**
   * Creates a new Server that listens for connections on the given port. Use port 0 to let the system
   * pick a free port.
   *
   * @param port the port on which this server listens for connections
   * @throws IOException if an I/O error occurs when opening the socket
   */
  protected GoServer(int port) throws IOException {
    super(port);
    queue = new ArrayList<>();
    connectionHandlerList = new ArrayList<>();
    games = new ArrayList<>();
  }

  public int getPort() { return super.getPort(); }


  @Override
  public void acceptConnections() throws IOException {
    super.acceptConnections();
  }

  @Override
  public void close() { super.close(); }

  /**
   * Handle connection is called by the socketserver when a connection attempt on the socket is made.
   * Creates a new connection handler for the given socket.
   * Assign the connection handler to a server connection, and the server
   * add the connection handler to the player list.
   * @param socket the socket for the connection
   * @return the connection handler
   */
  @Override
  protected void handleConnection(Socket socket) {
    try {
      ServerConnection serverConnection = new ServerConnection(socket);
      ConnectionHandler connectionHandler = new ConnectionHandler();
      connectionHandler.serverConnection = serverConnection;
      connectionHandler.goServer = this;
      serverConnection.connectionHandler = connectionHandler;
      serverConnection.start();
      addPlayer(connectionHandler);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Check if the request is coming from a player that has already logged on.
   * If they are already logged on add them to the queue.
   * @param player to add
   * @return if the process was successful.
   */
  protected synchronized boolean addToQueue(ConnectionHandler player) {
    if (connectionHandlerList.contains(player)) {
      queue.add(player);
      if (queue.size() == 2) {
        startGame();
      }
      return true;
    } else { return false; }
  }

  protected void addPlayer(ConnectionHandler player) {
    connectionHandlerList.add(player);
  }

  protected void removePlayer(ConnectionHandler player) {
    connectionHandlerList.remove(player);
  }

  /**
   * When a queue reaches the length of 2 a game is started with the players currently in the queue.
   * This game is added to the games list on the server
   */
  protected void startGame() {
    GoGame game = new GoGame(gameDimension, queue);
    queue.clear();
    games.add(game);
  }

  protected void receiveMove(Move move, ConnectionHandler player) {
    // Get the game that the player is taking part in.
    Game currentGame;
    for (Game game : games) {
      if (game.getPLayers().containsValue(player)) {
        currentGame = game;
        if (currentGame.validateMove(move, player)) {
          currentGame.updateState(move);
        }
      }
    }
  }
}
