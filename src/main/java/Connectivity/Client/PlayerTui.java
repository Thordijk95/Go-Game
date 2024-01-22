package Connectivity.Client;

import java.lang.foreign.PaddingLayout;

public class PlayerTui {

  public PlayerTui(){};

  public static void main(String[] args) {
    PlayerTui playerTui = new PlayerTui();
    playerTui.run();

  }


  public void run() {
    System.out.println();

  }

  public void help() {
    System.out.println("This is a player tui and serves as an interface to play as a player");
    System.out.println("This is a list of possible commands you might need:");
    System.out.println("--help will open this explanation.");
    System.out.println("LOGIN ~ <Username>");
  }
}
