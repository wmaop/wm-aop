package org.wmaop.interceptor.mock.exception;

import java.util.Map;

import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptResult;
import org.wmaop.interceptor.BaseInterceptor;

import com.wm.data.IData;

public class ExceptionInterceptor extends BaseInterceptor {

	public static final String MAP_EXCEPTION = "exception";
	
	private final InterceptResult interceptResult;

	public ExceptionInterceptor(String exceptionClassName) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		this((Exception) Class.forName(exceptionClassName).newInstance());
	}
	
	public ExceptionInterceptor(Exception e) {
		super("Exception:"+e.getClass().getName());
		interceptResult = new InterceptResult(true, e);
	}
	
	@Override
	public InterceptResult intercept(FlowPosition flowPosition, IData idata) {
		invokeCount++;
		return interceptResult;
	}

	@Override
	protected void addMap(Map<String, Object> am) {
		am.put(MAP_TYPE, "ExceptionInterceptor");
		am.put(MAP_EXCEPTION, interceptResult.getException().getClass().getName());
		
	}

}
