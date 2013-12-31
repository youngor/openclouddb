package org.hx.rainbow.common.logging;

import org.hx.rainbow.common.exception.SysException;


public class LogException extends SysException
{
  private static final long serialVersionUID = 1022924004852350942L;


  public LogException(String message)
  {
    super(message);
  }

  public LogException(String message, Throwable cause) {
    super(message, cause);
  }

  public LogException(Throwable cause) {
    super(cause);
  }
}