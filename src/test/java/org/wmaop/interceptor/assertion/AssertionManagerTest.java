package org.wmaop.interceptor.assertion;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;
import org.wmaop.aop.chainprocessor.Interceptor;
import org.wmaop.aop.pipeline.FlowPosition;
import static org.mockito.Mockito.*;

import com.wm.data.IDataFactory;

public class AssertionManagerTest {

	@Test
	public void shouldExerciseBasics() {
		AssertionManager asm = new AssertionManager();
		Assertable assertion = new AssertionInterceptor("foo intercepting assertion");
		asm.addAssertion("foo", assertion);
		assertEquals(1, asm.getAssertionNames().size());
		assertEquals(1, asm.getAssertions().size());
		assertEquals("foo", new ArrayList<Object>(asm.getAssertionNames()).get(0));
		
		assertEquals("foo intercepting assertion", asm.getAssertion("foo").getName());
		assertEquals(0, asm.getInvokeCount("foo"));
		((Interceptor)assertion).intercept(mock(FlowPosition.class), IDataFactory.create());
		assertEquals(1, asm.getInvokeCount("foo"));
	}

}
