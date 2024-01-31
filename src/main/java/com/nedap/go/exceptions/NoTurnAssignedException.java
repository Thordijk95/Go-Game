package com.nedap.go.exceptions;

public class NoTurnAssignedException extends Exception{

  public NoTurnAssignedException() { super("No player has been assigned to have the turn in the game."); }

}
