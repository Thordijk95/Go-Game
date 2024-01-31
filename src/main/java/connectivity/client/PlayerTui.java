package connectivity.client;

import game.Move;
import game.Stone;
import game.player.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class PlayerTui {
  private boolean connection;
  private PlayerPlayer player;

  public PlayerTui(){};

  public static void main(String[] args) {
    PlayerTui playerTui = new PlayerTui();
    playerTui.run();
  }

  public void run() {
    Scanner input = new Scanner(System.in);
    PlayerPlayer player = new PlayerPlayer();
    player.playerTui = this;
    while (!connection) {
      System.out.println("Please provide the InetAddress where to make a connection: ");
      InetAddress inetAddress = null;
      try {
        inetAddress = InetAddress.getByName(input.nextLine());
      } catch (UnknownHostException e) {
        System.out.println("Host unknown.");
        e.printStackTrace();
      }

      System.out.println("Please provide the port where to connect on: ");
      int port = input.nextInt();

      try {
        player.setPlayerConnection(inetAddress, port);
        System.out.println("Connection to the server established!");
        connection = true;
      } catch (IOException e) {
        System.out.println("IOException, please provide a new address and or portnumber");
        e.printStackTrace();
      }
    }
    help();
    String lastMessage = "";
    while (!lastMessage.equals("--quit")) {
      lastMessage = input.nextLine();
      if (!lastMessage.equals("--help") && !lastMessage.equals("")) {
        player.sendMessage(lastMessage);
      }
    }
    System.out.println("Quit received");
    System.exit(0);
  }

  public void help() {
    System.out.println("This is a player tui and serves as an interface to play as a player");
    System.out.println("This is a list of possible commands you might need:");
    System.out.println("--help will open this explanation.");
    System.out.println("LOGIN ~ <Username> to login to the server with a certain username");
    System.out.println("QUEUE to notify the server you want to placed in a queue");
    System.out.println("MOVE ~ <N> to play a stone at index n on the board");
    System.out.println("MOVE ~ <(X,Y)> to play a stone at the coordinate x, y");
    System.out.println("PASS");
    System.out.println("RESIGN");
    System.out.println("ERROR ~ <error message> to communicate an error to the server");
  }

  public Move determinMoveTui() {
    Scanner input = new Scanner(System.in);
    int index = input.nextInt();
    Stone stone = player.getColor() == "black" ? Stone.BLACK : Stone.WHITE;
    return new Move(stone, index);
  }

}
