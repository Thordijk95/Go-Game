package com.nedap.go.exceptions;

public class InvalidNameException extends Exception{

  public InvalidNameException(String message) { super(message); }

  public InvalidNameException() { this("Name does not meet the requirements."); }

}
