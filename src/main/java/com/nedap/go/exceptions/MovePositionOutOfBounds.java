package com.nedap.go.exceptions;

public class MovePositionOutOfBounds extends InvalidMoveException {

  public MovePositionOutOfBounds(String message) { super(message); }

  public MovePositionOutOfBounds() {this("The selected index is not on the board."); }

}
