package com.xlcatlin.wm.interceptor.bdd;

import com.wm.data.IData;
import com.xlcatlin.wm.aop.chainprocessor.InterceptResult;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;
import com.xlcatlin.wm.interceptor.mock.exception.ExceptionInterceptor;

public class ExceptionAction implements ThenAction {

	private final ExceptionInterceptor interceptor;
	
	ExceptionAction(String exc) {
		try {
			interceptor = new ExceptionInterceptor(exc);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}		
	}
	
	@Override
	public InterceptResult execute(FlowPosition flowPosition, IData idata) {
		return interceptor.intercept(flowPosition, idata);
	}

}
