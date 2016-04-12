package org.wmaop.aop.interceptor;

public class InterceptResult {
	
	public static final InterceptResult TRUE = new InterceptResult(true);
	public static final InterceptResult FALSE = new InterceptResult(false);

	private final boolean hasIntercepted;
	private final Exception exception;

	public InterceptResult(boolean hasIntercepted) {
		this.hasIntercepted = hasIntercepted;
		exception = null;
	}

	public InterceptResult(boolean hasIntercepted, Exception e) {
		this.exception = e;
		this.hasIntercepted = hasIntercepted;
	}

	public boolean hasIntercepted() {
		return hasIntercepted;
	}

	public Exception getException() {
		return exception;
	}
	
	
}
