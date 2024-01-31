package com.nedap.go.exceptions;

public class InvalidPlayerTurnException extends InvalidPlayerException {

  public InvalidPlayerTurnException() { super("Not your turn!"); }

}
