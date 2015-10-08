package org.wmaop.aop.matcher.jexl;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.wmaop.aop.matcher.MatchResult;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;

public class JexlIDataMatcherTest {

	@Test
	public void shouldHandleMultipleExpressions() {
		Map<String, String> exprs = new HashMap<>();
		exprs.put("expr1", "foo == 'a'");
		exprs.put("expr2", "foo == 'b'");
		JexlIDataMatcher jidm = new JexlIDataMatcher(exprs);

		IData idata = IDataFactory.create();
		IDataCursor idc = idata.getCursor();
		IDataUtil.put(idc, "foo", "x");
		assertFalse(jidm.match(idata).isMatch());

		IDataUtil.put(idc, "foo", "a");
		MatchResult m = jidm.match(idata);
		assertTrue(m.isMatch());
		assertEquals("expr1", m.getId());

		IDataUtil.put(idc, "foo", "b");
		m = jidm.match(idata);
		assertTrue(m.isMatch());
		assertEquals("expr2", m.getId());
	}

	@Test
	public void shouldThrowExceptionForInvalidExpression() {

		IData idata = IDataFactory.create();
		try {
			// assignment will fail
			new JexlIDataMatcher("id1", "foo = 2").match(idata);
			fail();
		} catch (Exception e) {
		}

	}

	@Test
	public void shouldVerifyMatch() {
		IData idata = IDataFactory.create();

		JexlIDataMatcher jidm = new JexlIDataMatcher("id1", "foo == 'bar'");
		assertEquals("JexlMatcher[foo == 'bar']", jidm.toString());

		assertFalse(jidm.match(idata).isMatch());

		IDataCursor idc = idata.getCursor();
		IDataUtil.put(idc, "foo", "bar");
		assertTrue(jidm.match(idata).isMatch());

	}

}
