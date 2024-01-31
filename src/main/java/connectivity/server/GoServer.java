package connectivity.server;

import com.nedap.go.exceptions.InvalidMoveException;
import com.nedap.go.exceptions.InvalidNameException;
import com.nedap.go.exceptions.InvalidPlayerGameException;
import com.nedap.go.exceptions.InvalidPlayerTurnException;
import com.nedap.go.exceptions.NoTurnAssignedException;
import connectivity.SocketServer;
import connectivity.protocol.GoProtocol;
import game.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GoServer extends SocketServer {
  private List<ConnectionHandler> players;
  private Queue<ConnectionHandler> queue;
  private List<Game> games;
  public int gameDimension = 9;
  private Lock lock = new ReentrantLock();

  /**
   * Creates a new Server that listens for connections on the given port. Use port 0 to let the system
   * pick a free port.
   *
   * @param port the port on which this server listens for connections
   * @throws IOException if an I/O error occurs when opening the socket
   */
  public GoServer(int port) throws IOException {
    super(port);
    queue = new LinkedList<>();
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
      connectionHandler.sendMessage(GoProtocol.HELLO + "~Please respond with - LOGIN ~ <username>");
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
  protected void addToQueue(ConnectionHandler player) {
    lock.lock();
    if (players.contains(player)) {
      queue.add(player);
      player.sendMessage(GoProtocol.QUEUED);
      if (queue.size() == 2) {
        startGame();
      }
    } else {
      player.sendMessage(GoProtocol.ERROR + "~Not logged in");
    }
    lock.unlock();
  }

  /**
   * Add a player to the list.
   * @param player to add
   * @return if adding was successfull.
   */
  protected boolean addPlayer(ConnectionHandler player) {
    lock.lock();
    try {
      for (ConnectionHandler handler : players) {
        if (player.getUsername().equals(handler.getUsername())) {
          // Player with username already exists
          declinePlayerAdded(player);
          throw new InvalidNameException("Name already taken");
        }
      }
    } catch (InvalidNameException e) {
      e.printStackTrace();
      lock.unlock();
      return false;
    }
    // Not already in the list, add them
    players.add(player);
    confirmPlayerAdded(player);
    lock.unlock();
    return true;
  }

  /**
   * Remove a player from the player list.
   * @param player to remove
   */
   protected void removePlayer(ConnectionHandler player) {
     lock.lock();
     try {
       players.remove(player);
       lock.unlock();
     } catch (IndexOutOfBoundsException | NullPointerException e) {
       System.out.println("Player was not registered in the player list.");
       e.printStackTrace();
       lock.unlock();
     }
   }

   /**
   * When a queue reaches the length of 2 a game is started with the players currently in the queue.
   * This game is added to the games list on the server
   */
  protected void startGame() {
    List<ConnectionHandler> players = new ArrayList<>();
    players.add(queue.remove());
    players.add(queue.remove());
    GoGame game = new GoGame(gameDimension, players);
    games.add(game);
    for (ConnectionHandler player : game.players) {
      player.sendMessage(GoProtocol.GAME_STARTED + "~" + players.getFirst().getUsername() + "," + players.getLast().getUsername() + "~" + gameDimension);
    }
    players.getFirst().sendMessage("MAKE MOVE");
  }

  protected void receiveMove(Move move, ConnectionHandler player) {
    // Get the game that the player is taking part in.
    Game game = null;
    try {
      game = getPlayerGame(player);
    } catch (InvalidPlayerGameException e) {
      e.printStackTrace();
    }
    try {
      if (game.getAtTurn() != player) {
        throw new InvalidPlayerTurnException();
      }
      if (game != null && game.validateMove(move, player) ) {
        game.updateState(move);
        switchTurn(game);
      }
    } catch (InvalidMoveException e){
      e.printStackTrace();
      refuseMove(player, e.getMessage());
    } catch (InvalidPlayerTurnException e) {
      e.printStackTrace();
      player.sendError(e.getMessage());
    } catch (NoTurnAssignedException e) {
      e.printStackTrace();
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
   * Retrieve all the games that are currently running.
   * @return running games
   */
  public List<Game> getGames() {
    return games;
  }

  /**
   * Get all the game that a player is playing in.
   * @param player for reference
   * @return the game the player is playing
   * @throws InvalidPlayerGameException when the player is not part of any game.
   */
  public Game getPlayerGame(ConnectionHandler player) throws InvalidPlayerGameException {
    try {
      for (Game game : games) {
        if (game.getPLayers().contains(player)) {
          return game;
        }
      }
      throw new InvalidPlayerGameException("Player is not part of any game");
    } catch (InvalidPlayerGameException e) {
      e.printStackTrace();
      return null;
    }
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
  private void declinePlayerAdded(ConnectionHandler player) {
    // Confirm a player has been added
    for (ConnectionHandler handler : players) {
      handler.sendMessage(GoProtocol.REJECTED +"~" + player.getUsername());
    }
  }

  private void switchTurn(Game game) throws NoTurnAssignedException {
    game.switchTurn();
    ConnectionHandler player = game.getAtTurn();
    player.sendMessage(GoProtocol.MAKE_MOVE);
  }



  private void refuseMove(ConnectionHandler player, String message) {
    player.sendMoveRefused(message);
  }
}
