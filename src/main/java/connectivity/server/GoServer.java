package connectivity.server;

import com.nedap.go.exceptions.InvalidMoveException;
import com.nedap.go.exceptions.InvalidNameException;
import com.nedap.go.exceptions.InvalidPlayerGameException;
import com.nedap.go.exceptions.InvalidPlayerTurnException;
import com.nedap.go.exceptions.NoTurnAssignedException;
import com.nedap.go.exceptions.PlayerNotLoggedInException;
import connectivity.SocketServer;
import connectivity.protocol.GoProtocol;
import game.*;

import game.player.Player;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GoServer extends SocketServer {
  private List<ConnectionHandler> connectedPlayers;
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
    connectedPlayers = new ArrayList<>();
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
   * @param queueingPlayer to add
   * @return if the process was successful.
   */
  protected void addToQueue(ConnectionHandler queueingPlayer){
    try {
      if (queueingPlayer.getLoggedIn()) {
        lock.lock();
        if (connectedPlayers.contains(queueingPlayer) && !queueingPlayer.getInQueue()) {
          queue.add(queueingPlayer);
          queueingPlayer.setInQueue();
          queueingPlayer.sendMessage(GoProtocol.QUEUED);
          if (queue.size() == 2) {
            startGame();
          }
        } else {
          removeFromQueue(queueingPlayer);
          queueingPlayer.sendMessage(GoProtocol.ERROR + "~Already in queue, now not in queue anymore");
        }
        lock.unlock();
      } else {
        throw new PlayerNotLoggedInException();
      }
    } catch (PlayerNotLoggedInException e) {
      queueingPlayer.sendError(e.getMessage());
    }
  }

  /**
   * Add a player to the list.
   * @param player to add
   * @return if adding was successfull.
   */
  protected boolean addPlayer(ConnectionHandler player) {
    lock.lock();
    try {
      for (ConnectionHandler handler : connectedPlayers) {
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
    connectedPlayers.add(player);
    player.setLoggedIn();
    confirmPlayerAdded(player);
    lock.unlock();
    return true;
  }

  /**
   * Remove a player from the player list.
   * @param player to remove
   */
   protected void removePlayer(ConnectionHandler player) {
     System.out.println("removeplayer");
     lock.lock();
     try {
       connectedPlayers.remove(player);
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

  protected void gameOver(Game game, String winner, String score) {
    List<ConnectionHandler> players = game.getPLayers();
    for(ConnectionHandler player : players) {
      player.sendMessage(GoProtocol.GAME_OVER+"~"+winner+"~"+score);
    }
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
      if (game != null && game.validateMove(move)) {
        informPlayers(move, player, game.getOtherPlayer(player));
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
      // Critical error, restart the game?
      System.out.println("Critical error");
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
        if(game.pass(player) == 2) {
          gameOver(game, game.getWinner().toString(), game.gameOverScore());
        }
      }
    }
  }

  /**
   * Player resigned, they loose regardless of the score.
   * @param player that resigned.
   */
  protected void receiveResign(ConnectionHandler player) {
    for (Game game : games) {
      if(game.getPLayers().contains(player)) {
        if(game.pass(player) == 2) {
          gameOver(game, game.getOtherPlayer(player).stone.toString(), game.gameOverScore());
        }
      }
    }
  }

  public List<ConnectionHandler> getPlayerConnectionHandles() {
    return connectedPlayers;
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
    player.sendMessage(GoProtocol.ACCEPTED +"~" + player.getUsername());
  }

  /**
   * Broadcast to all players that a player with a name has tried to login but has been rejected.
   * @param player that was rejected
   */
  private void declinePlayerAdded(ConnectionHandler player) {
    // Confirm a player has been added
    player.sendMessage(GoProtocol.REJECTED +"~" + player.getUsername());
  }

  private void switchTurn(Game game) throws NoTurnAssignedException {
    game.switchTurn();
    ConnectionHandler player = game.getAtTurn();
    player.sendMessage(GoProtocol.MAKE_MOVE);
  }

  private void refuseMove(ConnectionHandler player, String message) {
    player.sendMoveRefused(message);
  }

  private void informPlayers(Move move, ConnectionHandler player, ConnectionHandler opponent) {
    player.sendMove(move);
    opponent.sendMove(move);
  }

  private void removeFromQueue(ConnectionHandler player) {
    player.setInQueue();
    queue.remove(player);
  }

}
