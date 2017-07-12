package org.wmaop.interceptor.mock.exception;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptResult;
import org.wmaop.interceptor.BaseInterceptor;

import com.wm.data.IData;

public class ExceptionInterceptor extends BaseInterceptor {

	public static final String MAP_EXCEPTION = "exception";

	private final InterceptResult interceptResult;

	public ExceptionInterceptor(String exceptionClassName)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		this(exceptionClassName, null);
	}

	public ExceptionInterceptor(String exceptionClassName, String defaultMessage)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		super("Exception:" + exceptionClassName);
		Exception exception;
		int bktPos = exceptionClassName.indexOf('(');
		if (bktPos != -1) {
			String clazzName = exceptionClassName.substring(0, bktPos);
			String msg = getMessage(exceptionClassName, bktPos);
			exception = getException(clazzName, msg);
		} else if (defaultMessage != null) {
			exception = getException(exceptionClassName, defaultMessage);
		} else {
			exception = (Exception) Class.forName(exceptionClassName).newInstance();
		}
		interceptResult = new InterceptResult(true, exception);
	}

	private Exception getException(String clazzName, String msg) throws InstantiationException {
		try {
			Constructor<Exception> constructor = (Constructor<Exception>) Class.forName(clazzName).getDeclaredConstructor(String.class);
			constructor.setAccessible(true);
			return constructor.newInstance(msg);
		} catch (Throwable e) {
			throw new InstantiationException("Unable find or create exception " + clazzName);
		}
	}

	public ExceptionInterceptor(Exception e) {
		super("Exception:" + e.getClass().getName());
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

	private String getMessage(String classDeclaration, int bktPos) {
		int start = bktPos + 1;
		while (isSkippable(classDeclaration.charAt(start))) {
			start++;
		}
		int end = classDeclaration.length();
		while (isSkippable(classDeclaration.charAt(end - 1))) {
			end--;
		}
		return classDeclaration.substring(start, end);
	}

	private boolean isSkippable(char c) {
		switch (c) {
		case '\"':
			return true;
		case '(':
			return true;
		case ')':
			return true;
		default:
			return false;
		}
	}

}
