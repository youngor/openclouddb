package org.hx.rainbow.common.exception;

public class AppException extends RuntimeException {

	private static final long serialVersionUID = 8688953989589840707L;

	private Object[] args;
	private Object returnObj;

	public AppException(String msg) {
		super(msg);
	}

	public AppException(String msg, Object returnObj) {
		super(msg);
		this.returnObj = returnObj;
	}

	public AppException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public AppException(String msg, Throwable cause, Object returnObj) {
		super(msg, cause);
		this.returnObj = returnObj;
	}

	public AppException(String msg, Object[] args) {
		super(msg);
		this.args = args;
	}

	public AppException(String msg, Object[] args, Object returnObj) {
		super(msg);
		this.args = args;
		this.returnObj = returnObj;
	}

	public AppException(String msg, Object[] args, Throwable cause) {
		super(msg, cause);
		this.args = args;
	}

	public AppException(String msg, Object[] args, Throwable cause,
			Object returnObj) {
		super(msg, cause);
		this.args = args;
		this.returnObj = returnObj;
	}

	public AppException(Throwable cause) {
		super(cause);
	}

	public AppException(Throwable cause, Object returnObj) {
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
