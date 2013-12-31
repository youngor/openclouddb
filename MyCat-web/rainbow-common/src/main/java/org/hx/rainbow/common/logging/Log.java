package org.hx.rainbow.common.logging;


public abstract interface Log {
	  public abstract boolean isDebugEnabled();

	  public abstract void error(String s, Throwable t);

	  public abstract void error(String s);

	  public abstract boolean isInfoEnabled();

	  public abstract void info(String s);

	  public abstract void debug(String s);

	  public abstract void debug(String s, Throwable t);

	  public abstract boolean isWarnEnabled();

	  public abstract void warn(String s);

	  public abstract void warn(String s, Throwable t);

}