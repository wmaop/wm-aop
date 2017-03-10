package org.wmaop.interceptor.mock.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.wmaop.aop.interceptor.InterceptResult;

public class ExceptionInterceptorTest {

	@Test
	public void shouldHandleExceptionInstance() throws Exception {

		Exception exception = new Exception();
		ExceptionInterceptor ei = new ExceptionInterceptor(exception);
		
		InterceptResult ir = ei.intercept(null, null);
		
		assertEquals(1, ei.getInvokeCount());
		assertEquals(ir.getException(), exception);
	}
	
	@Test
	public void shouldHandleExceptionName() throws Exception {
		String exceptionClassName = "java.lang.RuntimeException";
		
		ExceptionInterceptor ei = new ExceptionInterceptor(exceptionClassName);
		
		InterceptResult ir = ei.intercept(null, null);
		
		assertEquals(ir.getException().getClass().getName(), exceptionClassName);
		assertEquals(ei.toMap().get("exception"), exceptionClassName);

	}

}
