package org.wmaop.interceptor.mock.exception;

import org.wmaop.aop.chainprocessor.InterceptResult;
import org.wmaop.aop.chainprocessor.Interceptor;
import org.wmaop.aop.pipeline.FlowPosition;

import com.wm.data.IData;

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
