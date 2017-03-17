package org.wmaop.interceptor.mock.exception;

import static org.junit.Assert.*;

import org.junit.Test;
import org.wmaop.aop.interceptor.InterceptResult;

public class ExceptionInterceptorTest {

	private static final String RUNTIME_EXCEPTION = "java.lang.RuntimeException";

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
		ExceptionInterceptor ei = new ExceptionInterceptor(RUNTIME_EXCEPTION);
		
		InterceptResult ir = ei.intercept(null, null);
		
		assertEquals(ir.getException().getClass().getName(), RUNTIME_EXCEPTION);
		assertEquals(ei.toMap().get("exception"), RUNTIME_EXCEPTION);
	}

	@Test
	public void shouldHandleExceptionNameWithMessage() throws Exception {
		checkValidMessage("java.lang.RuntimeException(\"MyException\")");
		checkValidMessage("java.lang.RuntimeException(MyException)");
		checkValidMessage("java.lang.RuntimeException(MyException");
		checkValidMessage("java.lang.RuntimeException(\"MyException)");
	}

	@Test
	public void shouldAcceptDefaultMessage() throws Exception {
		ExceptionInterceptor ei = new ExceptionInterceptor(RUNTIME_EXCEPTION, "DefaultMessage");
		Exception e = ei.intercept(null, null).getException();
		assertEquals(RUNTIME_EXCEPTION, e.getClass().getName());
		assertEquals("DefaultMessage", e.getMessage());
		assertEquals(RUNTIME_EXCEPTION, ei.toMap().get("exception"));
	}
	
	@Test
	public void shouldOverrideDefaultMessage() throws Exception {
		ExceptionInterceptor ei = new ExceptionInterceptor("java.lang.RuntimeException(MyOverride)", "DefaultMessage");
		Exception e = ei.intercept(null, null).getException();
		assertEquals(RUNTIME_EXCEPTION, e.getClass().getName());
		assertEquals("MyOverride", e.getMessage());
		assertEquals(RUNTIME_EXCEPTION, ei.toMap().get("exception"));
	}

	private void checkValidMessage(String exceptionClassName)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		ExceptionInterceptor ei = new ExceptionInterceptor(exceptionClassName);
		
		Exception e = ei.intercept(null, null).getException();
		
		assertEquals(RUNTIME_EXCEPTION, e.getClass().getName());
		assertEquals("MyException", e.getMessage());
		assertEquals(RUNTIME_EXCEPTION, ei.toMap().get("exception"));
	}

	@Test
	public void shouldHandleExceptionWithInvalidSyntax() throws Exception {
		String exceptionClassName = "java.lang.RuntimeException2(\"MyException\")";
		
		try {
			new ExceptionInterceptor(exceptionClassName);
			fail();
		} catch (Exception e) {
			assertTrue(e.getMessage().contains("Unable find or create exception"));
		}
	}
}
