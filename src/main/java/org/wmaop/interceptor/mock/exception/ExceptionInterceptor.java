package org.wmaop.interceptor.mock.exception;

import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptResult;
import org.wmaop.interceptor.BaseInterceptor;

import com.wm.data.IData;

public class ExceptionInterceptor extends BaseInterceptor {

	private final InterceptResult interceptResult;

	public ExceptionInterceptor(String e) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		this((Exception) Class.forName(e).newInstance());
	}
	
	public ExceptionInterceptor(Exception e) {
		super("Exception:"+e.getClass().getName());
		interceptResult = new InterceptResult(true, e);
	}
	public InterceptResult intercept(FlowPosition flowPosition, IData idata) {
		invokeCount++;
		return interceptResult;
	}

}
