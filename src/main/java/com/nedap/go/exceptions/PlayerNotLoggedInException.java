package com.nedap.go.exceptions;

public class PlayerNotLoggedInException extends InvalidPlayerException {
  public PlayerNotLoggedInException() { super("Player not logged, login first!"); }
}
