package com.xlcatlin.wm.aop.matcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.xlcatlin.wm.aop.InterceptPoint;
import com.xlcatlin.wm.aop.matcher.FlowPositionMatcher;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

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
