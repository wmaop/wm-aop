package org.wmaop.aop.interceptor;

public class InterceptionException extends RuntimeException {

	public InterceptionException(String message) {
		super(message);
	}

	public InterceptionException(Throwable cause) {
		super(cause);
	}

	public InterceptionException(String message, Throwable cause) {
		super(message, cause);
	}
}
