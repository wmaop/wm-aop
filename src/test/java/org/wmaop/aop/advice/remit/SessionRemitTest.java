package org.wmaop.aop.advice.remit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.wmaop.aop.advice.Scope;

public class SessionRemitTest {

	@Test
	public void shouldVerifyForKnownSessionID() {
		SessionRemit sr = new SessionRemit("foo");
		assertFalse(sr.isApplicable(null));
		assertFalse(sr.isApplicable(Scope.ALL));
	}
	
	@Test
	public void shouldVerifyWhenNoSession() {
		SessionRemit sr = new SessionRemit(SessionRemit.NO_SESSION);
		assertTrue(sr.isApplicable(Scope.ALL));
		assertTrue(sr.isApplicable(Scope.SESSION));
		assertFalse(sr.isApplicable(Scope.GLOBAL));
		assertFalse(sr.isApplicable(Scope.USER));
	}
		
	@Test
	public void shouldVerifyForDiscoveredSessionId() {
		assertTrue(new SessionRemit().isApplicable(Scope.SESSION));
		assertTrue(new SessionRemit().toString().contains(SessionRemit.NO_SESSION));
	}

}
