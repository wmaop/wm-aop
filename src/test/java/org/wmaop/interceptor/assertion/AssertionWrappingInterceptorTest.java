package org.wmaop.interceptor.assertion;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.wmaop.aop.chainprocessor.InterceptResult;
import org.wmaop.aop.chainprocessor.Interceptor;
import org.wmaop.aop.pipeline.FlowPosition;

import com.wm.data.IData;
import com.wm.data.IDataFactory;

public class AssertionWrappingInterceptorTest {

	@Test
	public void test() {
		Interceptor interceptor = mock(Interceptor.class);
		AssertionWrappingInterceptor awi = new AssertionWrappingInterceptor(interceptor , "foo");
		assertEquals("foo", awi.getName());
		assertEquals(0, awi.getInvokeCount());
		
		FlowPosition flowPosition = mock(FlowPosition.class);
		IData idata = IDataFactory.create();
		// Should not make a difference - counting invokes, not results
		when(interceptor.intercept(flowPosition, idata)).thenReturn(InterceptResult.FALSE);
		InterceptResult ir = awi.intercept(flowPosition, idata);
		assertEquals(1, awi.getInvokeCount());
		assertFalse(ir.hasIntercepted());
		
		when(interceptor.intercept(flowPosition, idata)).thenReturn(InterceptResult.TRUE);
		ir = awi.intercept(flowPosition, idata);
		assertEquals(2, awi.getInvokeCount());
		assertTrue(ir.hasIntercepted());
		
	}

}
