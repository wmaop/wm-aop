package com.xlcatlin.util.jexl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;

public class IDataJexlContextTest {

	@Test
	public void shouldReturnIDataValues() {
		IDataJexlContext idjc = new IDataJexlContext(getIData());

		assertTrue(idjc.has("k1"));
		assertEquals("v1", idjc.get("k1"));

		assertTrue(idjc.has("k2"));

		IDataJexlContext k2 = (IDataJexlContext) idjc.get("k2");
		assertTrue(k2.has("k21"));
		assertEquals("v21", k2.get("k21"));
	}

	private IData getIData() {
		IData idata = IDataFactory.create();
		IDataCursor cursor = idata.getCursor();
		IDataUtil.put(cursor, "k1", "v1");

		IData idata2 = IDataFactory.create();
		IDataCursor cursor2 = idata2.getCursor();
		IDataUtil.put(cursor2, "k21", "v21");

		IDataUtil.put(cursor, "k2", idata2);

		return idata;
	}
}
