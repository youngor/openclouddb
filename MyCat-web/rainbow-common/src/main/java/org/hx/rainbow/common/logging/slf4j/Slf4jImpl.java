package org.hx.rainbow.common.logging.slf4j;

import org.hx.rainbow.common.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jImpl implements Log {
	private Logger log;

	public Slf4jImpl(String clazz) {
		this.log = LoggerFactory.getLogger(clazz);
	}

	@Override
	public boolean isDebugEnabled() {
		return this.log.isDebugEnabled();
	}

	@Override
	public void error(String s, Throwable e) {
		this.log.error(s, e);
	}



	@Override
	public void error(String s) {
		this.log.error(s);
	}



	@Override
	public void debug(String s) {
		this.debug(s);
	}



	@Override
	public void warn(String s) {
		this.warn(s);
		
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
	public void debug(String s, Throwable e) {
		this.log.debug(s,e);
	}

	@Override
	public boolean isWarnEnabled() {
		return	this.log.isWarnEnabled();
	}

	@Override
	public void warn(String s, Throwable e) {
		this.log.warn(s, e);
		
	}

}