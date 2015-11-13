package org.wmaop.aop.matcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.wmaop.aop.matcher.FlowPositionMatcher;
import org.wmaop.aop.pipeline.FlowPosition;
import org.wmaop.aop.pointcut.InterceptPoint;

public class StringMatcherTest {

	@Test
	public void shouldMatch() {
		assertTrue(new FlowPositionMatcher("id", "foo:bar").match(new FlowPosition(InterceptPoint.INVOKE, "foo:bar")).isMatch());
		assertTrue(new FlowPositionMatcher("id", "foo").match(new FlowPosition(InterceptPoint.INVOKE, "foo")).isMatch());
	}

	@Test
	public void shouldNotMatch() {
		assertFalse(new FlowPositionMatcher("id", "foo").match(new FlowPosition(InterceptPoint.INVOKE, "foo:bar")).isMatch());
		assertFalse(new FlowPositionMatcher("id", "foo").match(new FlowPosition(InterceptPoint.INVOKE, null)).isMatch());
	}
}
