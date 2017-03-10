package org.wmaop.aop.advice.remit;

import static org.junit.Assert.*;

import org.junit.Test;
import org.wmaop.aop.advice.Scope;

public class UserRemitTest {

	@Test
	public void shouldReflectDiscoveredUserScope() {
		UserRemit ur = new UserRemit();
		assertFalse(ur.isApplicable(null));
		assertTrue(ur.isApplicable(Scope.ALL));
		assertFalse(ur.isApplicable(Scope.SESSION));
		assertFalse(ur.isApplicable(Scope.GLOBAL));
		assertTrue(ur.isApplicable(Scope.USER));

		assertTrue(ur.toString().contains(UserRemit.DEFAULT_USERNAME));
	}
	
	@Test
	public void shouldNotBeApplicableToOtherUser() {
		UserRemit ur = new UserRemit("foo");
		assertFalse(ur.isApplicable(Scope.USER));
	}

}
