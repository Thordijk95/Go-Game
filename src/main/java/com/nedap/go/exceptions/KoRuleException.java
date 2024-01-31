package com.nedap.go.exceptions;

public class KoRuleException extends InvalidMoveException{

  public KoRuleException() { super("Move does not follow the Ko rule!"); }

}
