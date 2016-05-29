package org.wmaop.aop.interceptor;

public interface AssertableInterceptor extends Interceptor {
	int getInvokeCount();
	String getName();
}
