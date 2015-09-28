package com.xlcatlin.wm.interceptor.assertion;

import com.wm.data.IData;
import com.xlcatlin.wm.aop.chainprocessor.InterceptResult;
import com.xlcatlin.wm.aop.chainprocessor.Interceptor;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

public class TrackingInterceptor implements Interceptor, Assertion {

	private int invocationCount;
	private final Interceptor wrappedInterceptor;
	
	TrackingInterceptor(Interceptor wrappedInterceptor) {
		this.wrappedInterceptor =wrappedInterceptor;
	}
	
	@Override
	public InterceptResult intercept(FlowPosition flowPosition, IData idata) {
		InterceptResult ir = wrappedInterceptor.intercept(flowPosition, idata);
		if (ir.hasIntercepted()) {
			invocationCount++;
		}
		return ir;
	}

	@Override
	public int getInvokeCount() {
		return invocationCount;
	}

}
