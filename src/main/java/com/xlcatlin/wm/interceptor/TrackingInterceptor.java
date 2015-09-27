package com.xlcatlin.wm.interceptor;

import com.wm.data.IData;
import com.xlcatlin.wm.aop.chainprocessor.InterceptResult;
import com.xlcatlin.wm.aop.chainprocessor.Interceptor;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

public abstract class TrackingInterceptor implements Interceptor {

	private int invocationCount;
	
	@Override
	public InterceptResult intercept(FlowPosition flowPosition, IData idata) {
		InterceptResult ir = interceptFlow(flowPosition, idata);
		if (ir.hasIntercepted()) {
			invocationCount++;
		}
		return ir;
	}
	
	public int getInvocationCount() {
		return invocationCount;
	}
	
	abstract InterceptResult interceptFlow(FlowPosition flowPosition, IData idata);

}
