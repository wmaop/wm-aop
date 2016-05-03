package org.wmaop.interceptor;

import org.wmaop.aop.interceptor.Interceptor;

public abstract class BaseInterceptor implements Interceptor {

	private final String assertionName;
	protected int invokeCount = 0;

	protected BaseInterceptor(String assertionName) {
		this.assertionName = assertionName;
	}
	
	@Override
	public int getInvokeCount() {
		return invokeCount;
	}

	@Override
	public String getName() {
		return assertionName;
	}

}
