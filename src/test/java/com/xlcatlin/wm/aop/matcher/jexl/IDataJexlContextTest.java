package com.xlcatlin.wm.aop.matcher.jexl;

import static org.junit.Assert.*;

import org.junit.Test;

import com.wm.data.BasicData;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;
import com.xlcatlin.util.jexl.IDataJexlContext;

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
		IData idata = new BasicData();
		IDataCursor cursor = idata.getCursor();
		IDataUtil.put(cursor , "k1", "v1");
		
		IData idata2 = new BasicData();
		IDataCursor cursor2 = idata2.getCursor();
		IDataUtil.put(cursor2 , "k21", "v21");
		
		IDataUtil.put(cursor , "k2", idata2);
		
		return idata;
	}
}
