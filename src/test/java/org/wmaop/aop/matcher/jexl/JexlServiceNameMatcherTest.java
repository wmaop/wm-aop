package org.wmaop.aop.matcher.jexl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptPoint;

public class JexlServiceNameMatcherTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void shouldMatch() {
		JexlServiceNameMatcher jsnm = new JexlServiceNameMatcher("alpha", "serviceName == 'foo'");
		FlowPosition flowPosition = new FlowPosition(InterceptPoint.INVOKE, "foo");
		assertEquals("serviceName == 'foo'", jsnm.toMap().get("expression"));
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
		thrown.expect(JexlParseException.class);
		new JexlServiceNameMatcher("alpha", "serviceName = 'foo'");
	}
}
