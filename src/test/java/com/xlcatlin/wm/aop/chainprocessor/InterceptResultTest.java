package com.xlcatlin.wm.aop.chainprocessor;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xlcatlin.wm.aop.chainprocessor.InterceptResult;

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
