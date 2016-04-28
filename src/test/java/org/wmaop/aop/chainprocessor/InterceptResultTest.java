package org.wmaop.aop.chainprocessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.wmaop.aop.interceptor.InterceptResult;

public class InterceptResultTest {

	@Test
	public void shouldHaveCorrectDefaults() {
		assertTrue(InterceptResult.TRUE.hasIntercepted());
		assertFalse(InterceptResult.FALSE.hasIntercepted());
		assertNull(InterceptResult.TRUE.getException());
		assertNull(InterceptResult.FALSE.getException());
	}

	@Test
	public void shouldHaveException() {
		Exception e = new Exception();
		InterceptResult ir = new InterceptResult(true, e);
		assertEquals(e, ir.getException());
		assertTrue(ir.hasIntercepted());
	}
}
