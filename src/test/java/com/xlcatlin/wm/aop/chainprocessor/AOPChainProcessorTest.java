package com.xlcatlin.wm.aop.chainprocessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Iterator;

import org.junit.Test;
import org.mockito.Mockito;

import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.invoke.ServiceStatus;
import com.wm.data.IData;
import com.wm.lang.ns.NSName;
import com.wm.util.coder.IDataXMLCoder;
import com.xlcatlin.wm.aop.Advice;
import com.xlcatlin.wm.aop.InterceptPoint;
import com.xlcatlin.wm.aop.PointCut;
import com.xlcatlin.wm.aop.chainprocessor.AOPChainProcessor;
import com.xlcatlin.wm.aop.matcher.AlwaysTrueMatcher;
import com.xlcatlin.wm.aop.matcher.Matcher;
import com.xlcatlin.wm.aop.matcher.FlowPositionMatcher;
import com.xlcatlin.wm.aop.matcher.jexl.JexlIDataMatcher;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;
import com.xlcatlin.wm.aop.pointcut.ServicePipelinePointCut;
import com.xlcatlin.wm.interceptor.assertion.Assertion;
import com.xlcatlin.wm.interceptor.mock.canned.CannedResponseInterceptor;

public class AOPChainProcessorTest {

	@Test
	public void shouldExecuteConditionalMatch() throws Exception {
		ClassLoader classLoader = this.getClass().getClassLoader();
		AOPChainProcessor cp = new AOPChainProcessor();
		cp.setEnabled(true);
		
		FlowPositionMatcher serviceNameMatcher = new FlowPositionMatcher("my id",  "pre:foo");
		Matcher<IData> pipelineMatcher = new JexlIDataMatcher("doc", "documentName == 'iso'");
		Assertion assertion = new Assertion("myAssertion");
		Advice assertionAdvice = new Advice("adv1", new ServicePipelinePointCut(serviceNameMatcher, pipelineMatcher), assertion, InterceptPoint.BEFORE);
		cp.registerAdvice(assertionAdvice );
		
		CannedResponseInterceptor interceptor = new CannedResponseInterceptor(classLoader.getResourceAsStream("cannedResponse.xml"));
		Advice interceptAdvice = new Advice("adv2", new ServicePipelinePointCut(serviceNameMatcher, pipelineMatcher), interceptor, InterceptPoint.INVOKE);
		cp.registerAdvice(interceptAdvice);

		// Pipeline mocking
		IData idata = new IDataXMLCoder().decode(classLoader.getResourceAsStream("pipeline.xml"));
		BaseService baseService = Mockito.mock(BaseService.class);
		when(baseService.getNSName()).thenReturn(NSName.create("pre:foo"));
		ServiceStatus ss = Mockito.mock(ServiceStatus.class);
		
		@SuppressWarnings("rawtypes")
		Iterator chainIterator = new Iterator() {
			public boolean hasNext() {
				return false;
			}
			public Object next() {
				return null;
			}
		};
		
		// Execute
		cp.process(chainIterator, baseService, idata, ss );
		
		assertTrue(((Assertion)cp.getAdvice("adv1").getInterceptor()).hasAsserted());
		assertEquals(1, assertion.getInvokeCount());
	}

	@Test
	public void shouldExecuteAlwaysTrueReponse() throws Exception {
		ClassLoader classLoader = this.getClass().getClassLoader();
		AOPChainProcessor cp = new AOPChainProcessor();
		cp.setEnabled(true);
		
		FlowPositionMatcher serviceNameMatcher = new FlowPositionMatcher("my id",  "pre:foo");
		CannedResponseInterceptor interceptor = new CannedResponseInterceptor(classLoader.getResourceAsStream("cannedResponse.xml"));
		ServicePipelinePointCut pointCut = new ServicePipelinePointCut(serviceNameMatcher, new AlwaysTrueMatcher());
		Advice advice = new Advice("intercept", pointCut, interceptor, InterceptPoint.INVOKE);
		cp.registerAdvice(advice);
		
		// Pipeline mocking
		IData idata = new IDataXMLCoder().decode(classLoader.getResourceAsStream("pipeline.xml"));
		BaseService baseService = Mockito.mock(BaseService.class);
		when(baseService.getNSName()).thenReturn(NSName.create("pre:foo"));
		ServiceStatus ss = Mockito.mock(ServiceStatus.class);
		
		@SuppressWarnings("rawtypes")
		Iterator chainIterator = new Iterator() {
			public boolean hasNext() {
				return false;
			}
			public Object next() {
				return null;
			}
		};
		
		// Execute
		cp.process(chainIterator, baseService, idata, ss );
		assertTrue(new String(new IDataXMLCoder().encodeToBytes(idata)).contains("\"apple\">alpha"));
	}
}
