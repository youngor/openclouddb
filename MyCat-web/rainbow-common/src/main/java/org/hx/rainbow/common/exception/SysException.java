package org.hx.rainbow.common.exception;

public class SysException  extends RuntimeException {

	private static final long serialVersionUID = 3116483353040779859L;
	
	private Object[] args;
	private Object returnObj;

	public SysException(String msg) {
		super(msg);
	}

	public SysException(String msg, Object returnObj) {
		super(msg);
		this.returnObj = returnObj;
	}

	public SysException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public SysException(String msg, Throwable cause, Object returnObj) {
		super(msg, cause);
		this.returnObj = returnObj;
	}

	public SysException(String msg, Object[] args) {
		super(msg);
		this.args = args;
	}

	public SysException(String msg, Object[] args, Object returnObj) {
		super(msg);
		this.args = args;
		this.returnObj = returnObj;
	}

	public SysException(String msg, Object[] args, Throwable cause) {
		super(msg, cause);
		this.args = args;
	}

	public SysException(String msg, Object[] args, Throwable cause,
			Object returnObj) {
		super(msg, cause);
		this.args = args;
		this.returnObj = returnObj;
	}

	public SysException(Throwable cause) {
		super(cause);
	}

	public SysException(Throwable cause, Object returnObj) {
		super(cause);
		this.returnObj = returnObj;
	}

	public Object[] getArgs() {
		return this.args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Object getReturnObj() {
		return this.returnObj;
	}

	public Throwable fillInStackTrace() {
		return this;
	}
}
