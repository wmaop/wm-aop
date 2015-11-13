package org.wmaop.interceptor.bdd;

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
import org.wmaop.aop.advice.Advice;
import org.wmaop.aop.chainprocessor.AOPChainProcessor;
import org.wmaop.interceptor.assertion.Assertable;
import org.wmaop.interceptor.assertion.AssertionManager;
import org.wmaop.interceptor.bdd.BddInterceptor;
import org.wmaop.interceptor.bdd.BddParser;

import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.invoke.InvokeChainProcessor;
import com.wm.app.b2b.server.invoke.ServiceStatus;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import com.wm.lang.ns.NSName;

public class BddInterceptorTest {

	@Test
	public void shouldAssert() throws Exception {
		ClassLoader classLoader = this.getClass().getClassLoader();
		AOPChainProcessor cp = new AOPChainProcessor();
		cp.setEnabled(true);
		Advice advice = new BddParser().parse(classLoader.getResourceAsStream("bdd/assertionBdd.xml"));
		cp.registerAdvice(advice);

		BddInterceptor bddi = (BddInterceptor)advice.getInterceptor();
		assertEquals(1, bddi.getInterceptorsOfType(Assertable.class).size());
		
		// Pipeline mocking
		IData pipeline = IDataFactory.create();
		ServiceStatus ss = mock(ServiceStatus.class);

		Iterator<InvokeChainProcessor> chainIterator = new ArrayList<InvokeChainProcessor>().iterator();

		// Execute a service, no change to pipeline
		cp.process(chainIterator, getBaseService("pub.test:svcA"), pipeline, ss);
		
		AssertionManager asm = cp.getAssertionManager();
		// Correct service, condition doesnt match
		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		assertEquals(0, asm.getAssertion("PreBarAssertion").getInvokeCount());
		assertTrue(asm.verifyNever("PreBarAssertion"));
		assertFalse(asm.verifyOnceOnly("PreBarAssertion"));
		assertFalse(asm.verifyAtLeast(1, "PreBarAssertion"));
		assertFalse(asm.verifyAtLeastOnce("PreBarAssertion"));
		assertTrue(asm.verifyAtMost(1, "PreBarAssertion"));
		
		// Correct service, condition  match
		add(pipeline, "foo", 2);
		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		assertEquals(1, asm.getAssertion("PreBarAssertion").getInvokeCount());
		assertFalse(asm.verifyNever("PreBarAssertion"));
		assertTrue(asm.verifyOnceOnly("PreBarAssertion"));
		assertTrue(asm.verifyAtLeast(1, "PreBarAssertion"));
		assertTrue(asm.verifyAtLeastOnce("PreBarAssertion"));
		assertTrue(asm.verifyAtMost(1, "PreBarAssertion"));

		cp.process(chainIterator, getBaseService("com.catlin.foo:bar"), pipeline, ss);
		assertEquals(2, asm.getAssertion("PreBarAssertion").getInvokeCount());
		assertFalse(asm.verifyNever("PreBarAssertion"));
		assertFalse(asm.verifyOnceOnly("PreBarAssertion"));
		assertTrue(asm.verifyAtLeast(1, "PreBarAssertion"));
		assertTrue(asm.verifyAtLeastOnce("PreBarAssertion"));
		assertFalse(asm.verifyAtMost(1, "PreBarAssertion"));
	
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
