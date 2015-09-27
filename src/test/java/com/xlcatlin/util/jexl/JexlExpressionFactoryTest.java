package com.xlcatlin.util.jexl;

import static org.junit.Assert.*;

import org.apache.commons.jexl2.Expression;
import org.junit.Test;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;

public class JexlExpressionFactoryTest {

	@Test
	public void test() {
		Expression expr = JexlExpressionFactory.createExpression("alpha.beta == \"hello\"");
	}

	@Test
	public void shouldMatchPipeline() throws Exception {
		Expression expr = JexlExpressionFactory.createExpression("foo == 2");
		IData idata = IDataFactory.create();
		Boolean result = (Boolean) expr.evaluate(new IDataJexlContext(idata));
		assertFalse(result);
		put(idata, "foo", 2);
		result = (Boolean) expr.evaluate(new IDataJexlContext(idata));
		assertTrue(result);
		put(idata, "foo", 1);
		result = (Boolean) expr.evaluate(new IDataJexlContext(idata));
		assertFalse(result);
	}
	
	void put(IData idata, String k, Object v) {
		IDataCursor cursor = idata.getCursor();
		IDataUtil.put(cursor, k, v);
		cursor.destroy();
	}
}
