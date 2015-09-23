package com.xlcatlin.wm.aop.matcher.jexl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.xlcatlin.wm.aop.InterceptPoint;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

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
			JexlServiceNameMatcher jsnm = new JexlServiceNameMatcher("alpha", "serviceName = 'foo'");
			fail();
		} catch (Exception e) {
			// NOOP
		}
	}
}
