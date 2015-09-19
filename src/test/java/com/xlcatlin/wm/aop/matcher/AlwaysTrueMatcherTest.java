package com.xlcatlin.wm.aop.matcher;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xlcatlin.wm.aop.matcher.AlwaysTrueMatcher;

public class AlwaysTrueMatcherTest {

	@Test
	public void shouldBeTrue() {
		AlwaysTrueMatcher atm = new AlwaysTrueMatcher();
		assertTrue(atm.match("kfhkfjhk").isMatch());
		assertEquals("undefined", atm.match("gggkg").getId());
	}

}
