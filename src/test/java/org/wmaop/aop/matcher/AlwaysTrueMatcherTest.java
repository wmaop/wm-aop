package org.wmaop.aop.matcher;

import static org.junit.Assert.*;

import org.junit.Test;
import org.wmaop.aop.matcher.AlwaysTrueMatcher;

public class AlwaysTrueMatcherTest {

	@Test
	public void shouldBeAlwaysTrue() {
		AlwaysTrueMatcher<Object> atm = new AlwaysTrueMatcher<Object>("foo");
		assertTrue(atm.match("kfhkfjhk").isMatch());
		assertEquals("foo", atm.match("gggkg").getId());
		assertEquals("foo", atm.toMap().get("id"));
	}

}
