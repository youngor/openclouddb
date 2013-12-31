package org.hx.rainbow.common.logging.nologging;

import org.hx.rainbow.common.logging.Log;

public class NoLoggingImpl implements Log {
	public NoLoggingImpl(String clazz) {
	}



	@Override
	public boolean isInfoEnabled() {
		return false;
	}

	@Override
	public void info(String paramString) {
	}

	@Override
	public void debug(String paramString, Throwable paramThrowable) {
		
	}

	@Override
	public boolean isWarnEnabled() {
		return false;
	}

	@Override
	public void warn(String paramString, Throwable paramThrowable) {
		
	}



	@Override
	public boolean isDebugEnabled() {
		
		return false;
	}



	@Override
	public void error(String paramString, Throwable paramThrowable) {
		
		
	}



	@Override
	public void error(String paramString) {
		
		
	}



	@Override
	public void debug(String paramString) {
		
		
	}



	@Override
	public void warn(String paramString) {
		
		
	}
}