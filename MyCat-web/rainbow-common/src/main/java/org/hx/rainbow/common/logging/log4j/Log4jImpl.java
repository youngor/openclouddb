package org.hx.rainbow.common.logging.log4j;


import org.apache.log4j.Logger;
import org.hx.rainbow.common.logging.Log;

public class Log4jImpl implements Log {
	private Logger log;

	public Log4jImpl(String clazz) {
		this.log = Logger.getLogger(clazz);
	}

	@Override
	public boolean isDebugEnabled() {
		return this.log.isDebugEnabled();
	}

	@Override
	public void error(String s, Throwable t) {
		this.log.error(s, t);
	}

	@Override
	public void error(String s) {
		this.log.error(s);
	}

	@Override
	public boolean isInfoEnabled() {
		return this.log.isInfoEnabled();
	}

	@Override
	public void info(String s) {
		this.log.info(s);
	}

	@Override
	public void debug(String s) {
		this.log.debug(s);
	}

	@Override
	public void debug(String s, Throwable t) {
		this.log.debug(s, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return false;
	}

	@Override
	public void warn(String s) {
		 this.log.warn(s);
	}

	@Override
	public void warn(String s, Throwable t) {
		this.log.warn(s, t);
	}

	


}