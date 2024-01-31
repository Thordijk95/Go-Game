package com.nedap.go.exceptions;

public class IntersectionOccupiedException extends InvalidMoveException {
  public IntersectionOccupiedException(String message) { super(message); }

  public IntersectionOccupiedException() { this("Intersection already occupied."); }

}
