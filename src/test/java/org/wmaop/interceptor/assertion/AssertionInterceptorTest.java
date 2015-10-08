package org.wmaop.interceptor.assertion;

import static org.junit.Assert.*;

import org.junit.Test;
import org.wmaop.aop.pipeline.FlowPosition;

import com.wm.data.IData;

import static org.mockito.Mockito.*;

public class AssertionInterceptorTest {

	@Test
	public void shouldExerciseAll() {
		AssertionInterceptor ai = new AssertionInterceptor("foo");
		assertEquals("foo", ai.getName());
		assertEquals(0, ai.getInvokeCount());
		assertFalse(ai.hasAsserted());
	
		ai.intercept(mock(FlowPosition.class), mock(IData.class));
		assertEquals(1, ai.getInvokeCount());
		assertTrue(ai.hasAsserted());
		
		ai.reset();
		assertEquals(0, ai.getInvokeCount());
		assertFalse(ai.hasAsserted());
	}

}
