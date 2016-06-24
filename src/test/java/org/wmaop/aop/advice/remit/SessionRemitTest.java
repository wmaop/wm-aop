package org.wmaop.aop.advice.remit;

import static org.junit.Assert.*;

import org.junit.Test;
import org.wmaop.aop.advice.Scope;

public class SessionRemitTest {

	@Test
	public void shouldVerify() {
		SessionRemit sr = new SessionRemit("foo");
		assertFalse(sr.isApplicable(null));
		assertFalse(sr.isApplicable(Scope.ALL));
		
		sr = new SessionRemit("NoSession");
		assertTrue(sr.isApplicable(Scope.ALL));
		assertTrue(sr.isApplicable(Scope.SESSION));
		assertFalse(sr.isApplicable(Scope.GLOBAL));
		assertFalse(sr.isApplicable(Scope.USER));
		
		
		assertTrue(new SessionRemit().isApplicable(Scope.SESSION));
		assertTrue(new SessionRemit().toString().contains("NoSession"));
	}

}
