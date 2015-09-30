package com.xlcatlin.wm.interceptor.bdd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Files;
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
import com.xlcatlin.wm.aop.Advice;
import com.xlcatlin.wm.aop.chainprocessor.AOPChainProcessor;
import com.xlcatlin.wm.interceptor.assertion.Assertion;
import com.xlcatlin.wm.interceptor.assertion.AssertionManager;

public class BddInterceptorTest {

	@Test
	public void shouldAssert() throws Exception {
		ClassLoader classLoader = this.getClass().getClassLoader();
		AOPChainProcessor cp = new AOPChainProcessor();
		cp.setEnabled(true);
		Advice advice = new BddParser().parse(classLoader.getResourceAsStream("bdd/assertionBdd.xml"));
		cp.registerAdvice(advice);

		BddInterceptor bddi = (BddInterceptor)advice.getInterceptor();
		assertEquals(1, bddi.getInterceptorsOfType(Assertion.class).size());
		
		// Pipeline mocking
		IData pipeline = IDataFactory.create();
		ServiceStatus ss = mock(ServiceStatus.class);

		Iterator<InvokeChainProcessor> chainIterator = new ArrayList<InvokeChainProcessor>().iterator();

		// Execute a service, no change to pipeline
		cp.process(chainIterator, getBaseService("pub.test:svcA"), pipeline, ss);
		
		// Correct service, condition doesnt match
		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		assertEquals(0, AssertionManager.getInstance().getAssertion("PreBarAssertion").getInvokeCount());
		assertTrue(AssertionManager.getInstance().verifyNever("PreBarAssertion"));
		assertFalse(AssertionManager.getInstance().verifyOnceOnly("PreBarAssertion"));
		assertFalse(AssertionManager.getInstance().verifyAtLeast(1, "PreBarAssertion"));
		assertFalse(AssertionManager.getInstance().verifyAtLeastOnce("PreBarAssertion"));
		assertTrue(AssertionManager.getInstance().verifyAtMost(1, "PreBarAssertion"));
		
		// Correct service, condition  match
		add(pipeline, "foo", 2);
		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		assertEquals(1, AssertionManager.getInstance().getAssertion("PreBarAssertion").getInvokeCount());
		assertFalse(AssertionManager.getInstance().verifyNever("PreBarAssertion"));
		assertTrue(AssertionManager.getInstance().verifyOnceOnly("PreBarAssertion"));
		assertTrue(AssertionManager.getInstance().verifyAtLeast(1, "PreBarAssertion"));
		assertTrue(AssertionManager.getInstance().verifyAtLeastOnce("PreBarAssertion"));
		assertTrue(AssertionManager.getInstance().verifyAtMost(1, "PreBarAssertion"));

		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		assertEquals(2, AssertionManager.getInstance().getAssertion("PreBarAssertion").getInvokeCount());
		assertFalse(AssertionManager.getInstance().verifyNever("PreBarAssertion"));
		assertFalse(AssertionManager.getInstance().verifyOnceOnly("PreBarAssertion"));
		assertTrue(AssertionManager.getInstance().verifyAtLeast(1, "PreBarAssertion"));
		assertTrue(AssertionManager.getInstance().verifyAtLeastOnce("PreBarAssertion"));
		assertFalse(AssertionManager.getInstance().verifyAtMost(1, "PreBarAssertion"));
	
	}

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

	@Test
	public void shouldExecuteMultipleReturnsWithDefault() throws Exception {
		AOPChainProcessor cp = getConfiguredProcessor("bdd/multipleReturnBdd.xml");

		// Pipeline mocking
		IData pipeline = IDataFactory.create();
		ServiceStatus ss = mock(ServiceStatus.class);
		Iterator<InvokeChainProcessor> chainIterator = new ArrayList<InvokeChainProcessor>().iterator();

		// No change to pipeline, not fired
		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		assertEquals(null, get(pipeline, "apple"));

		// Service condition so should set default
		add(pipeline, "foo", 2);
		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		assertEquals("gamma", get(pipeline, "apple"));

		add(pipeline, "input", 1);
		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		assertEquals("alpha", get(pipeline, "apple"));

		add(pipeline, "input", 2);
		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		assertEquals("beta", get(pipeline, "apple"));
	}
	
	@Test
	public void shouldExecuteServiceAndWhenConditions() throws Exception {
		AOPChainProcessor cp = getConfiguredProcessor("bdd/multipleReturnWithElseBdd.xml");
		// Pipeline mocking
		IData pipeline = IDataFactory.create();
		ServiceStatus ss = mock(ServiceStatus.class);
		Iterator<InvokeChainProcessor> chainIterator = new ArrayList<InvokeChainProcessor>().iterator();

		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		assertEquals("gamma", get(pipeline, "apple"));

		add(pipeline, "input", 1);
		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		assertEquals("alpha", get(pipeline, "apple"));

		add(pipeline, "input", 2);
		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		assertEquals("beta", get(pipeline, "apple"));
	}
	
	@Test
	public void shouldReturnWithoutElse() throws Exception {
		AOPChainProcessor cp = getConfiguredProcessor("bdd/multipleReturnWithoutElseBdd.xml");

		// Pipeline mocking
		IData pipeline = IDataFactory.create();
		ServiceStatus ss = mock(ServiceStatus.class);
		Iterator<InvokeChainProcessor> chainIterator = new ArrayList<InvokeChainProcessor>().iterator();

		// No change to pipeline, not fired
		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		assertEquals(null, get(pipeline, "apple"));

		add(pipeline, "input", 1);
		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		assertEquals("alpha", get(pipeline, "apple"));

		add(pipeline, "input", 2);
		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		assertEquals("beta", get(pipeline, "apple"));
	}
	

	@Test
	public void shouldCapturePipeline() throws Exception {
		File f = new File("target/testCapture-1.xml");
		f.delete();
		assertFalse(f.exists());
		
		AOPChainProcessor cp = getConfiguredProcessor("bdd/pipelineCaptureBdd.xml");
		// Pipeline mocking
		IData pipeline = IDataFactory.create();
		ServiceStatus ss = mock(ServiceStatus.class);
		Iterator<InvokeChainProcessor> chainIterator = new ArrayList<InvokeChainProcessor>().iterator();
		
		add(pipeline, "foo", 2);
		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		
		f = new File("target/testCapture-1.xml");
		assertTrue(f.exists());
		assertTrue(new String(Files.readAllBytes(f.toPath())).contains("<number name=\"foo\" type=\"java.lang.Integer\">2</number>"));
		
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
