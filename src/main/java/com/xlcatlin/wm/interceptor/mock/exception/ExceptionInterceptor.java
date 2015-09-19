package com.xlcatlin.wm.interceptor.mock.exception;

import com.wm.data.IData;
import com.xlcatlin.wm.aop.chainprocessor.InterceptResult;
import com.xlcatlin.wm.aop.chainprocessor.Interceptor;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

public class ExceptionInterceptor implements Interceptor {

	private final InterceptResult interceptResult;

	public ExceptionInterceptor(String e) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		this((Exception) Class.forName(e).newInstance());
	}
	
	public ExceptionInterceptor(Exception e) {
		interceptResult = new InterceptResult(true, e);
	}
	public InterceptResult intercept(FlowPosition flowPosition, IData idata) {
		return interceptResult;
	}

}
