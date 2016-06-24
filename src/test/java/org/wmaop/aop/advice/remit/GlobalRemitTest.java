package org.wmaop.aop.advice.remit;

import static org.junit.Assert.*;

import org.junit.Test;
import org.wmaop.aop.advice.Scope;

public class GlobalRemitTest {

	@Test
	public void test() {
		GlobalRemit gr = new GlobalRemit();
		assertTrue(gr.isApplicable());
		assertTrue(gr.isApplicable(Scope.ALL));
		assertTrue(gr.isApplicable(Scope.GLOBAL));
		assertFalse(gr.isApplicable(Scope.USER));
		assertFalse(gr.isApplicable(Scope.SESSION));
	}

}
