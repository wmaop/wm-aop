package org.wmaop.aop.matcher.jexl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.wmaop.aop.matcher.jexl.JexlServiceNameMatcher;
import org.wmaop.aop.pipeline.FlowPosition;
import org.wmaop.aop.pointcut.InterceptPoint;

public class JexlServiceNameMatcherTest {

	@Test
	public void shouldMatch() {
		JexlServiceNameMatcher jsnm = new JexlServiceNameMatcher("alpha", "serviceName == 'foo'");
		FlowPosition flowPosition = new FlowPosition(InterceptPoint.INVOKE, "foo");
		assertTrue(jsnm.match(flowPosition).isMatch());
	}

	@Test
	public void shouldNotMatch() {
		JexlServiceNameMatcher jsnm = new JexlServiceNameMatcher("alpha", "serviceName == 'bar'");
		FlowPosition flowPosition = new FlowPosition(InterceptPoint.INVOKE, "foo");
		assertFalse(jsnm.match(flowPosition).isMatch());
	}

	@Test
	public void shouldFail() {
		try {
			new JexlServiceNameMatcher("alpha", "serviceName = 'foo'");
			fail();
		} catch (Exception e) {
			// NOOP
		}
	}
}
