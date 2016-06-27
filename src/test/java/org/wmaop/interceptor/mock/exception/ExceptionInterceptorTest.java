package org.wmaop.interceptor.mock.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.wmaop.aop.interceptor.InterceptResult;

public class ExceptionInterceptorTest {

	@Test
	public void shouldReturnExceptionInterceptResult() throws Exception {

		Exception exception = new Exception();
		ExceptionInterceptor ei = new ExceptionInterceptor(exception);
		InterceptResult ir = ei.intercept(null, null);
		assertEquals(ir.getException(), exception);

		ei = new ExceptionInterceptor("java.lang.RuntimeException");
		ir = ei.intercept(null, null);
		assertEquals(ir.getException().getClass().getName(), "java.lang.RuntimeException");
		assertEquals(ei.toMap().get("exception"), "java.lang.RuntimeException");

	}

}
