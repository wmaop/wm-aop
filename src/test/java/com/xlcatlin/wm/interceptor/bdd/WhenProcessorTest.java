package com.xlcatlin.wm.interceptor.bdd;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.bind.JAXBException;

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
		AOPChainProcessor cp = getConfiguredProcessor("bdd/cannedReturnBdd.xml");
		
		// Pipeline mocking
		IData pipeline = IDataFactory.create();
		ServiceStatus ss = mock(ServiceStatus.class);

		Iterator<InvokeChainProcessor> chainIterator = new ArrayList<InvokeChainProcessor>().iterator();

		// Execute a service, no change to pipeline
		cp.process(chainIterator, getBaseService("pub.test:svcA"), pipeline, ss);
		assertEquals(null, get(pipeline, "apple"));
		
		// Execute mocked service, pipeline changed
		cp.process(chainIterator, getBaseService("pub.test:svcB"), pipeline, ss);
		assertEquals("alpha", get(pipeline, "apple"));
		assertEquals("beta", get(pipeline, "pear"));
	}

	@Test
	public void shouldSetException() throws Exception {
		AOPChainProcessor cp = getConfiguredProcessor("bdd/exceptionBdd.xml");
		
		// Pipeline mocking
		IData pipeline = IDataFactory.create();
		IData alpha = IDataFactory.create();
		add(alpha, "beta", "abc");
		add(pipeline, "alpha", alpha);
		
		ServiceStatus ss = mock(ServiceStatus.class);

		Iterator<InvokeChainProcessor> chainIterator = new ArrayList<InvokeChainProcessor>().iterator();

		// Execute a service, no change to pipeline
		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		verify(ss, times(0)).setException(isA(Exception.class));
		
		// Execute mocked service, pipeline changed
		add(alpha, "beta", "hello");
		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		verify(ss, times(1)).setException(isA(Exception.class));
	}

	private AOPChainProcessor getConfiguredProcessor(String testXmlFileName) throws Exception {
		ClassLoader classLoader = this.getClass().getClassLoader();
		AOPChainProcessor cp = new AOPChainProcessor();
		cp.setEnabled(true);
		
		Advice advice = new BddParser().parse(classLoader.getResourceAsStream(testXmlFileName));
		cp.registerAdvice(advice);
		return cp;
	}

	
	private BaseService getBaseService(String svcName) {
		BaseService baseService = mock(BaseService.class);
		when(baseService.getNSName()).thenReturn(NSName.create(svcName));
		return baseService;
	}
	
	private void add(IData idata, String k, Object v) {
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
