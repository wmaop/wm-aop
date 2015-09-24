package com.xlcatlin.wm.interceptor.bdd;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.invoke.InvokeChainProcessor;
import com.wm.app.b2b.server.invoke.ServiceStatus;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import com.wm.lang.ns.NSName;
import com.wm.util.coder.IDataXMLCoder;
import com.xlcatlin.wm.aop.Advice;
import com.xlcatlin.wm.aop.chainprocessor.AOPChainProcessor;

public class WhenProcessorTest {

	@Test
	public void shouldFireCannedReturn() throws Exception {
		ClassLoader classLoader = this.getClass().getClassLoader();
		AOPChainProcessor cp = new AOPChainProcessor();
		cp.setEnabled(true);
		
		Advice advice = new BddParser().parse(classLoader.getResourceAsStream("bdd/cannedReturnBdd.xml"));
		cp.registerAdvice(advice);
		
		// Pipeline mocking
		IData pipeline = IDataFactory.create();
		ServiceStatus ss = mock(ServiceStatus.class);

		Iterator<InvokeChainProcessor> chainIterator = new ArrayList<InvokeChainProcessor>().iterator();

		// Execute
		cp.process(chainIterator, getBaseService("pub.test:svcB"), pipeline, ss);
		
		assertEquals("alpha", get(pipeline, "apple"));
		
	}

	private BaseService getBaseService(String svcName) {
		BaseService baseService = mock(BaseService.class);
		when(baseService.getNSName()).thenReturn(NSName.create(svcName));
		return baseService;
	}
	private void add(IData idata, String k, String v) {
		IDataCursor cursor = idata.getCursor();
		IDataUtil.put(cursor, k, v);
		cursor.destroy();
	}
	
	private Object get(IData idata, String k) {
		IDataCursor cursor = idata.getCursor();
		Object o = IDataUtil.get(cursor, k);
		cursor.destroy();
		return o;
	}
}
