package org.wmaop.aop.chainprocessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.wmaop.aop.advice.Advice;
import org.wmaop.aop.chainprocessor.AOPChainProcessor;
import org.wmaop.aop.chainprocessor.Interceptor;
import org.wmaop.aop.matcher.AlwaysTrueMatcher;
import org.wmaop.aop.matcher.FlowPositionMatcherImpl;
import org.wmaop.aop.matcher.Matcher;
import org.wmaop.aop.matcher.jexl.JexlIDataMatcher;
import org.wmaop.aop.pointcut.InterceptPoint;
import org.wmaop.aop.pointcut.PointCut;
import org.wmaop.aop.pointcut.ServicePipelinePointCut;
import org.wmaop.interceptor.assertion.AssertionInterceptor;
import org.wmaop.interceptor.mock.canned.CannedResponseInterceptor;
import org.wmaop.interceptor.mock.exception.ExceptionInterceptor;

import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.invoke.InvokeChainProcessor;
import com.wm.app.b2b.server.invoke.ServiceStatus;
import com.wm.data.IData;
import com.wm.lang.ns.NSName;
import com.wm.util.coder.IDataXMLCoder;

public class AOPChainProcessorTest {

	@Test
	public void shouldExecuteConditionalMatch() throws Exception {
		ClassLoader classLoader = this.getClass().getClassLoader();
		AOPChainProcessor cp = new AOPChainProcessor();
		cp.setEnabled(true);

		FlowPositionMatcherImpl serviceNameMatcher = new FlowPositionMatcherImpl("my id", "pre:foo");
		Matcher<IData> pipelineMatcher = new JexlIDataMatcher("doc", "documentName == 'iso'");
		AssertionInterceptor assertion = new AssertionInterceptor("myAssertion");
		Advice assertionAdvice = new Advice("adv1", new ServicePipelinePointCut(serviceNameMatcher, pipelineMatcher, InterceptPoint.BEFORE), assertion);
		cp.registerAdvice(assertionAdvice);
		assertEquals(1, cp.listAdvice().size());

		CannedResponseInterceptor interceptor = new CannedResponseInterceptor(classLoader.getResourceAsStream("cannedResponse.xml"));
		Advice interceptAdvice = new Advice("adv2", new ServicePipelinePointCut(serviceNameMatcher, pipelineMatcher, InterceptPoint.INVOKE), interceptor);
		cp.registerAdvice(interceptAdvice);
		
		assertEquals(2, cp.listAdvice().size());

		// Pipeline mocking
		IData idata = new IDataXMLCoder().decode(classLoader.getResourceAsStream("pipeline.xml"));
		BaseService baseService = mock(BaseService.class);
		when(baseService.getNSName()).thenReturn(NSName.create("pre:foo"));
		ServiceStatus ss = mock(ServiceStatus.class);

		Iterator<InvokeChainProcessor> chainIterator = new ArrayList<InvokeChainProcessor>().iterator();

		// Execute
		cp.process(chainIterator, baseService, idata, ss);

		assertTrue(((AssertionInterceptor) cp.getAdvice("adv1").getInterceptor()).hasAsserted());
		assertEquals(1, assertion.getInvokeCount());
		
		cp.clearAdvice();
		assertEquals(0, cp.listAdvice().size());

	}

	@Test
	public void shouldExecuteAlwaysTrueReponse() throws Exception {
		ClassLoader classLoader = this.getClass().getClassLoader();
		AOPChainProcessor cp = new AOPChainProcessor();
		cp.setEnabled(true);

		FlowPositionMatcherImpl serviceNameMatcher = new FlowPositionMatcherImpl("my id", "pre:foo");
		CannedResponseInterceptor interceptor = new CannedResponseInterceptor(classLoader.getResourceAsStream("cannedResponse.xml"));
		ServicePipelinePointCut pointCut = new ServicePipelinePointCut(serviceNameMatcher, new AlwaysTrueMatcher<IData>("my id"), InterceptPoint.INVOKE);
		Advice advice = new Advice("intercept", pointCut, interceptor);
		cp.registerAdvice(advice);

		// Pipeline mocking
		IData idata = new IDataXMLCoder().decode(classLoader.getResourceAsStream("pipeline.xml"));
		BaseService baseService = mock(BaseService.class);
		when(baseService.getNSName()).thenReturn(NSName.create("pre:foo"));
		ServiceStatus ss = mock(ServiceStatus.class);

		Iterator<InvokeChainProcessor> chainIterator = new ArrayList<InvokeChainProcessor>().iterator();

		// Execute
		cp.process(chainIterator, baseService, idata, ss);
		assertTrue(new String(new IDataXMLCoder().encodeToBytes(idata)).contains("\"apple\">alpha"));
	}

	@Test
	public void shouldUnregister() {
		AOPChainProcessor cp = new AOPChainProcessor();

		Interceptor interceptor = mock(Interceptor.class);
		PointCut pc = mock(PointCut.class);
		when(pc.getInterceptPoint()).thenReturn(InterceptPoint.INVOKE);
		Advice mockAdviceA = new Advice("a", pc, interceptor);
		cp.registerAdvice(mockAdviceA);
		assertEquals(1, cp.listAdvice().size());

		Advice mockAdviceB = new Advice("b", pc, interceptor);
		cp.registerAdvice(mockAdviceB);
		assertEquals(2, cp.listAdvice().size());

		List<Advice> advices = cp.listAdvice();
		assertEquals("a", advices.get(0).getId());
		assertEquals("b", advices.get(1).getId());

		cp.unregisterAdvice("a");
		advices = cp.listAdvice();
		assertEquals(1, advices.size());
		assertEquals("b", advices.get(0).getId());

		cp.registerAdvice(mockAdviceA);
		assertEquals(2, cp.listAdvice().size());
		
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
	public void shouldSetException() throws Exception{
		ClassLoader classLoader = this.getClass().getClassLoader();
		AOPChainProcessor cp = new AOPChainProcessor();
		cp.setEnabled(true);

		FlowPositionMatcherImpl serviceNameMatcher = new FlowPositionMatcherImpl("my id", "pre:foo");
		Exception exception = new Exception();
		Interceptor interceptor = new ExceptionInterceptor(exception );
		ServicePipelinePointCut pointCut = new ServicePipelinePointCut(serviceNameMatcher, new AlwaysTrueMatcher<IData>("my id"), InterceptPoint.INVOKE);
		Advice advice = new Advice("intercept", pointCut, interceptor);
		cp.registerAdvice(advice);

		// Pipeline mocking
		IData idata = new IDataXMLCoder().decode(classLoader.getResourceAsStream("pipeline.xml"));
		BaseService baseService = mock(BaseService.class);
		when(baseService.getNSName()).thenReturn(NSName.create("pre:foo"));
		ServiceStatus ss = mock(ServiceStatus.class);

		Iterator<InvokeChainProcessor> chainIterator = new ArrayList<InvokeChainProcessor>().iterator();

		// Execute
		cp.process(chainIterator, baseService, idata, ss);

		verify(ss, times(1)).setException(exception);
	}

	@Test
	public void shouldExecuteNextChainStepWhenNotInvoked() throws Exception {
		ClassLoader classLoader = this.getClass().getClassLoader();
		AOPChainProcessor cp = new AOPChainProcessor();
		cp.setEnabled(true);

		// Pipeline mocking
		IData idata = new IDataXMLCoder().decode(classLoader.getResourceAsStream("pipeline.xml"));
		BaseService baseService = mock(BaseService.class);
		when(baseService.getNSName()).thenReturn(NSName.create("pre:foo"));
		ServiceStatus ss = mock(ServiceStatus.class);

		InvokeChainProcessor icp = mock(InvokeChainProcessor.class);
		Iterator<InvokeChainProcessor> chainIterator = Arrays.asList(icp).iterator();
		
		// Execute
		cp.process(chainIterator, baseService, idata, ss);

		verify(icp, times(1)).process(chainIterator, baseService, idata, ss);
	}
	@Test
	public void shouldExecuteNextChainStepWhenDisabled() throws Exception {
		ClassLoader classLoader = this.getClass().getClassLoader();
		AOPChainProcessor cp = new AOPChainProcessor();

		// Pipeline mocking
		IData idata = new IDataXMLCoder().decode(classLoader.getResourceAsStream("pipeline.xml"));
		BaseService baseService = mock(BaseService.class);
		when(baseService.getNSName()).thenReturn(NSName.create("pre:foo"));
		ServiceStatus ss = mock(ServiceStatus.class);

		InvokeChainProcessor icp = mock(InvokeChainProcessor.class);
		Iterator<InvokeChainProcessor> chainIterator = Arrays.asList(icp).iterator();
		
		// Execute
		cp.process(chainIterator, baseService, idata, ss);

		verify(icp, times(1)).process(chainIterator, baseService, idata, ss);
	}
}
