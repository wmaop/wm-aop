package com.xlcatlin.wm.aop.matcher.jexl;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import com.xlcatlin.wm.aop.matcher.MatchResult;

public class JexlWrappingMatcherTest {

	@Test
	public void shouldParseSingleExpression() {
		JexlWrappingMatcher matcher = new JexlWrappingMatcher("foo", "foo == 2");
		IData idata = IDataFactory.create();
		add(idata, "foo", 2);
		assertTrue(matcher.match(idata).isMatch());
	}

	@Test
	public void shouldParseMultipleExpressions() {
		Map<String, String> exprs = new HashMap<>();
		exprs.put("foo", "foo == 2");
		exprs.put("bar", "bar == 1");
		JexlWrappingMatcher matcher = new JexlWrappingMatcher(exprs);
		IData idata = IDataFactory.create();
		assertFalse(matcher.match(idata).isMatch());
		add(idata, "foo", 2);
		MatchResult match = matcher.match(idata);
		assertEquals("foo", match.getId());
		assertTrue(match.isMatch());
		add(idata, "foo", 1);
		assertFalse(matcher.match(idata).isMatch());
		add(idata, "bar", 1);
		match = matcher.match(idata);
		assertEquals("bar", match.getId());
		assertTrue(match.isMatch());
	}
	
	private void add(IData idata, String k, Object v) {
		IDataCursor cursor = idata.getCursor();
		IDataUtil.put(cursor, k, v);
		cursor.destroy();
	}

}
