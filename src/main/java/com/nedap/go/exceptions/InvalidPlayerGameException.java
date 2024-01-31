package com.nedap.go.exceptions;

public class InvalidPlayerGameException extends InvalidPlayerException {

  public InvalidPlayerGameException(String message) { super(message); }

  public InvalidPlayerGameException() { this("The player is not part a game"); }
}
