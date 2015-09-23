package com.xlcatlin.wm.aop.chainprocessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.invoke.ServiceStatus;
import com.wm.data.IData;
import com.wm.lang.ns.NSName;
import com.wm.util.coder.IDataXMLCoder;
import com.xlcatlin.wm.aop.Advice;
import com.xlcatlin.wm.aop.InterceptPoint;
import com.xlcatlin.wm.aop.PointCut;
import com.xlcatlin.wm.aop.matcher.AlwaysTrueMatcher;
import com.xlcatlin.wm.aop.matcher.FlowPositionMatcher;
import com.xlcatlin.wm.aop.matcher.Matcher;
import com.xlcatlin.wm.aop.matcher.jexl.JexlIDataMatcher;
import com.xlcatlin.wm.aop.pointcut.ServicePipelinePointCut;
import com.xlcatlin.wm.interceptor.assertion.Assertion;
import com.xlcatlin.wm.interceptor.mock.canned.CannedResponseInterceptor;

public class AOPChainProcessorTest {

	@Test
	public void shouldExecuteConditionalMatch() throws Exception {
		ClassLoader classLoader = this.getClass().getClassLoader();
		AOPChainProcessor cp = new AOPChainProcessor();
		cp.setEnabled(true);

		FlowPositionMatcher serviceNameMatcher = new FlowPositionMatcher("my id", "pre:foo");
		Matcher<IData> pipelineMatcher = new JexlIDataMatcher("doc", "documentName == 'iso'");
		Assertion assertion = new Assertion("myAssertion");
		Advice assertionAdvice = new Advice("adv1", new ServicePipelinePointCut(serviceNameMatcher, pipelineMatcher, InterceptPoint.BEFORE), assertion);
		cp.registerAdvice(assertionAdvice);

		CannedResponseInterceptor interceptor = new CannedResponseInterceptor(classLoader.getResourceAsStream("cannedResponse.xml"));
		Advice interceptAdvice = new Advice("adv2", new ServicePipelinePointCut(serviceNameMatcher, pipelineMatcher, InterceptPoint.INVOKE), interceptor);
		cp.registerAdvice(interceptAdvice);

		// Pipeline mocking
		IData idata = new IDataXMLCoder().decode(classLoader.getResourceAsStream("pipeline.xml"));
		BaseService baseService = mock(BaseService.class);
		when(baseService.getNSName()).thenReturn(NSName.create("pre:foo"));
		ServiceStatus ss = mock(ServiceStatus.class);

		@SuppressWarnings("rawtypes")
		Iterator chainIterator = new Iterator() {
			public boolean hasNext() {
				return false;
			}

			public Object next() {
				return null;
			}

			public void remove() {
			}
		};

		// Execute
		cp.process(chainIterator, baseService, idata, ss);

		assertTrue(((Assertion) cp.getAdvice("adv1").getInterceptor()).hasAsserted());
		assertEquals(1, assertion.getInvokeCount());
	}

	@Test
	public void shouldExecuteAlwaysTrueReponse() throws Exception {
		ClassLoader classLoader = this.getClass().getClassLoader();
		AOPChainProcessor cp = new AOPChainProcessor();
		cp.setEnabled(true);

		FlowPositionMatcher serviceNameMatcher = new FlowPositionMatcher("my id", "pre:foo");
		CannedResponseInterceptor interceptor = new CannedResponseInterceptor(classLoader.getResourceAsStream("cannedResponse.xml"));
		ServicePipelinePointCut pointCut = new ServicePipelinePointCut(serviceNameMatcher, new AlwaysTrueMatcher(), InterceptPoint.INVOKE);
		Advice advice = new Advice("intercept", pointCut, interceptor);
		cp.registerAdvice(advice);

		// Pipeline mocking
		IData idata = new IDataXMLCoder().decode(classLoader.getResourceAsStream("pipeline.xml"));
		BaseService baseService = mock(BaseService.class);
		when(baseService.getNSName()).thenReturn(NSName.create("pre:foo"));
		ServiceStatus ss = mock(ServiceStatus.class);

		@SuppressWarnings("rawtypes")
		Iterator chainIterator = new Iterator() {
			public boolean hasNext() {
				return false;
			}

			public Object next() {
				return null;
			}

			public void remove() {
			}
		};

		// Execute
		cp.process(chainIterator, baseService, idata, ss);
		assertTrue(new String(new IDataXMLCoder().encodeToBytes(idata)).contains("\"apple\">alpha"));
	}

	@Test
	public void shouldUnregister() {
		AOPChainProcessor cp = new AOPChainProcessor();

		PointCut pc = mock(PointCut.class);
		when(pc.getInterceptPoint()).thenReturn(InterceptPoint.INVOKE);
		Advice mockAdviceA = new Advice("a", pc, null);
		cp.registerAdvice(mockAdviceA);

		Advice mockAdviceB = new Advice("b", pc, null);
		cp.registerAdvice(mockAdviceB);

		List<Advice> advices = cp.listAdvice();
		assertEquals("a", advices.get(0).getId());
		assertEquals("b", advices.get(1).getId());

		cp.unregisterAdvice("a");
		advices = cp.listAdvice();
		assertEquals(1, advices.size());
		assertEquals("b", advices.get(0).getId());

		cp.registerAdvice(mockAdviceA);
		cp.unregisterAdvice(mockAdviceA);
		advices = cp.listAdvice();
		assertEquals(1, advices.size());
		assertEquals("b", advices.get(0).getId());
	}

	@Test
	public void shouldClearAdvice() {
		AOPChainProcessor cp = new AOPChainProcessor();
		AOPChainProcessor.getInstance();

		PointCut pc = mock(PointCut.class);
		when(pc.getInterceptPoint()).thenReturn(InterceptPoint.INVOKE);
		Advice mockAdviceA = new Advice("a", pc, null);
		cp.registerAdvice(mockAdviceA);

		Advice mockAdviceB = new Advice("b", pc, null);
		cp.registerAdvice(mockAdviceB);

		List<Advice> advices = cp.listAdvice();
		assertEquals(2, advices.size());
		cp.clearAdvice();
		assertEquals(0, cp.listAdvice().size());

	}

	@Test
	public void shouldEnableDisable() {
		AOPChainProcessor cp = new AOPChainProcessor();
		assertFalse(cp.isEnabled());
		cp.setEnabled(true);
		assertTrue(cp.isEnabled());
		cp.setEnabled(false);
		assertFalse(cp.isEnabled());
	}

	@Test
	public void shouldSetException() {
		fail();
	}

	@Test
	public void shouldExecuteNextChainStep() {
		fail();
	}
}
