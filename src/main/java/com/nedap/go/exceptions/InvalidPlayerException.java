package com.nedap.go.exceptions;

public class InvalidPlayerException extends Exception{

  public InvalidPlayerException(String message) { super(message); }

  public InvalidPlayerException() { this("Invalid Player"); }

}
