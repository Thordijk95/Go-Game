package com.nedap.go.exceptions;

public class InvalidMoveException extends Exception{

  public InvalidMoveException(String message) { super(message); }

  public InvalidMoveException() { this("Wrong move argument"); }
}
