package org.hx.rainbow.common.logging.jdk14;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.hx.rainbow.common.logging.Log;

public class Jdk14LoggingImpl implements Log {
	private Logger log;

	public Jdk14LoggingImpl(String clazz) {
		this.log = Logger.getLogger(clazz);
	}


	@Override
	public boolean isInfoEnabled() {
		return this.log.isLoggable(Level.INFO);
	}

	@Override
	public void info(String s) {
		this.log.info(s);
	}

	@Override
	public void debug(String s, Throwable t) {
		this.log.log(Level.FINE, s, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return this.log.isLoggable(Level.WARNING);
	}

	@Override
	public void warn(String s, Throwable t) {
		this.log.log(Level.WARNING, s, t);
	}


	@Override
	public boolean isDebugEnabled() {
		return this.log.isLoggable(Level.FINE);
	}


	@Override
	public void error(String s, Throwable t) {
		this.log.log(Level.SEVERE, s, t);
	}


	@Override
	public void error(String s) {
		this.log.log(Level.SEVERE, s);
	}


	@Override
	public void debug(String s) {
		this.log.log(Level.FINE, s);
	}


	@Override
	public void warn(String s) {
		this.log.log(Level.WARNING, s);
	}
}