package org.wmaop.interceptor.assertion;

import org.wmaop.aop.interceptor.AssertableInterceptor;
import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptResult;
import org.wmaop.aop.interceptor.Interceptor;

import com.wm.data.IData;

public class AssertionWrappingInterceptor implements AssertableInterceptor {

	private int invocationCount;
	private final Interceptor wrappedInterceptor;
	private final String name;

	public AssertionWrappingInterceptor(Interceptor wrappedInterceptor, String name) {
		this.wrappedInterceptor = wrappedInterceptor;
		this.name = name;
	}

	public InterceptResult intercept(FlowPosition flowPosition, IData idata) {
		invocationCount++;
		return wrappedInterceptor.intercept(flowPosition, idata);
	}

	@Override
	public int getInvokeCount() {
		return invocationCount;
	}

	@Override
	public String getName() {
		return name;
	}

}
