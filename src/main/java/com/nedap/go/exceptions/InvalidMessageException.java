package com.nedap.go.exceptions;

public class InvalidMessageException extends Exception{

  public InvalidMessageException(String message) { super(message); }

  public InvalidMessageException() {this("Message is improperly formulated"); }

}
