package connectivity.server;

import connectivity.SocketServer;
import connectivity.protocol.GoProtocol;
import game.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GoServer extends SocketServer {
  private List<ConnectionHandler> players;
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
  public GoServer(int port) throws IOException {
    super(port);
    queue = new ArrayList<>();
    players = new ArrayList<>();
    games = new ArrayList<>();
  }

  @Override
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
  protected synchronized void addToQueue(ConnectionHandler player) {
    if (players.contains(player)) {
      queue.add(player);
      if (queue.size() == 2) {
        startGame();
      }
    }
  }

  protected boolean addPlayer(ConnectionHandler player) {
    for (ConnectionHandler handler : players) {
      if (player.getUsername().equals(handler.getUsername())) {
        // Player with username already exists
        declinelayerAdded(player);
        return false;
      }
    }
    System.out.println("Adding player on the server side");
    players.add(player);
    confirmPlayerAdded(player);
    return true;
  }

  protected void removePlayer(ConnectionHandler player) {
    players.remove(player);
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
    for (Game game : games) {
      if (game.getPLayers().contains(player)) {
        List<ConnectionHandler> players = game.getPLayers();
        if (game.validateMove(move, player)) {
          game.updateState(move);

        }
      }
    }
  }

  /**
   * A pass is received from one of the players. Check what game they are a part of and process the
   * pass.
   * @param player that played a pass.
   */
  protected void receivePass(ConnectionHandler player) {
    for (Game game : games) {
      if(game.getPLayers().contains(player)) {
        game.pass(player);
      }
    }
  }

  /**
   * Player resigned, they loose regardless of the score.
   * @param player that resigned.
   */
  protected void receiveResign(ConnectionHandler player) {
    for (Game game : games) {
      game.gameOverResign(player);
    }
  }

  public List<ConnectionHandler> getPlayers() {
    return players;
  }

  /**
   * Broadcast to all players that a player with a name has logged in to the server.
   * @param player that logged in
   */
  private void confirmPlayerAdded(ConnectionHandler player) {
    // Confirm a player has been added
    for (ConnectionHandler handler : players) {
      handler.sendMessage(GoProtocol.ACCEPTED +"~" + player.getUsername());
    }
  }

  /**
   * Broadcast to all players that a player with a name has tried to login but has been rejected.
   * @param player that was rejected
   */
  private void declinelayerAdded(ConnectionHandler player) {
    // Confirm a player has been added
    for (ConnectionHandler handler : players) {
      handler.sendMessage(GoProtocol.REJECTED +"~" + player.getUsername());
    }
  }
}
