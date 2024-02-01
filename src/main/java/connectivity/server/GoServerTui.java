package connectivity.server;

import java.io.IOException;
import java.util.Scanner;

public class GoServerTui {

  private GoServer goServer;

  public GoServerTui() {
  }

  public static void main(String[] args) {
    GoServerTui serverTui = new GoServerTui();
    serverTui.run();
  }

  private void acceptConnections() {
    try {
      goServer.acceptConnections();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void run() {
    Integer dimension = 9;
    Scanner input = new Scanner(System.in);
    System.out.println("Please provide the port where to start the server:");
    int port = input.nextInt();
    try {
      goServer = new GoServer(port);
      System.out.println("Server opened port " + port);
      new Thread(this::acceptConnections).start();     // Start accepting incoming connections
    } catch (IOException e) {
      System.out.println("Starting the server failed");
      e.printStackTrace();
    }
    help();
    String lastMessage = "";
    while (!lastMessage.equals("--quit")) {
      lastMessage = input.next();
      if (!lastMessage.isEmpty()) {
        String[] splitString = lastMessage.replace(" ", "").split("~");
        switch (splitString[0]) {
          case "--dimension":
            try {
              dimension = Integer.parseInt(splitString[0]);
              goServer.gameDimension = dimension;
            } catch (NumberFormatException e) {
              System.out.println("not a number format, try again");
            }
            break;
          case "--help": {
            help();
            break;
          }
          default: {
            System.out.println("That is not a valid input.");
            help();
          }
        }
      }
    }
  }

  public void help() {
    System.out.println("This is the help of the GoServerTui");
    System.out.println("--help provides the user with this menu");
    System.out.println("The standard dimensions of Go Games being started is 9x9");
    System.out.println("--dimension ~ <N> allows the user to set the dimensions of new Go Games.");
    System.out.println("--quit stops the server");
  }

}
