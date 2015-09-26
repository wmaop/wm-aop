package com.xlcatlin.wm.aop.matcher;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xlcatlin.wm.aop.matcher.AlwaysTrueMatcher;

public class AlwaysTrueMatcherTest {

	@Test
	public void shouldBeTrue() {
		AlwaysTrueMatcher<Object> atm = new AlwaysTrueMatcher<Object>("foo");
		assertTrue(atm.match("kfhkfjhk").isMatch());
		assertEquals("foo", atm.match("gggkg").getId());
	}

}
