package org.wmaop.aop.matcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptPoint;

public class FlowPositionMatcherTest {

	@Test
	public void shouldMatch() {
		assertTrue(new FlowPositionMatcherImpl("id", "foo:bar").match(new FlowPosition(InterceptPoint.INVOKE, "foo:bar")).isMatch());
		assertTrue(new FlowPositionMatcherImpl("id", "foo").match(new FlowPosition(InterceptPoint.INVOKE, "foo")).isMatch());
		assertEquals("serviceName", new FlowPositionMatcherImpl("id", "serviceName").toMap().get("serviceName"));
	}

	@Test
	public void shouldNotMatch() {
		assertFalse(new FlowPositionMatcherImpl("id", "foo").match(new FlowPosition(InterceptPoint.INVOKE, "foo:bar")).isMatch());
		assertFalse(new FlowPositionMatcherImpl("id", "foo").match(new FlowPosition(InterceptPoint.INVOKE, null)).isMatch());
	}
}
