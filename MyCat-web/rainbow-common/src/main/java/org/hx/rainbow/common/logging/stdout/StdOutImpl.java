package org.hx.rainbow.common.logging.stdout;

import org.hx.rainbow.common.logging.Log;

public class StdOutImpl implements Log {

	public StdOutImpl(String clazz) {
	}

	public boolean isDebugEnabled() {
		return true;
	}

	public boolean isTraceEnabled() {
		return true;
	}

	public void error(String s, Throwable e) {
		System.err.println(s);
		e.printStackTrace(System.err);
	}


	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	@Override
	public void info(String s) {
		System.out.println(s);
		
	}

	@Override
	public void debug(String s, Throwable t) {
		System.out.println(s);
		t.printStackTrace(System.out);
	}

	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	@Override
	public void warn(String s, Throwable t) {
		System.out.println(s);
		t.printStackTrace(System.out);
	}

	@Override
	public void error(String s) {
		System.out.println(s);
		
	}

	@Override
	public void debug(String s) {
		System.out.println(s);
		
	}

	@Override
	public void warn(String s) {
		System.out.println(s);
		
	}
}