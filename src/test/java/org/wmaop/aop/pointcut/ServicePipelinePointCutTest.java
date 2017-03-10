package org.wmaop.aop.pointcut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptPoint;
import org.wmaop.aop.matcher.FlowPositionMatcher;
import org.wmaop.aop.matcher.MatchResult;
import org.wmaop.aop.matcher.Matcher;

import com.wm.data.IData;

public class ServicePipelinePointCutTest {

	private FlowPosition pipelinePosition;
	private FlowPositionMatcher serviceNameMatcher;
	private IData idata;
	private Matcher<? super IData> pipelineMatcher;

	@Before
	@SuppressWarnings("unchecked")
	public void setup() {
		pipelinePosition = mock(FlowPosition.class);
		serviceNameMatcher = mock(FlowPositionMatcher.class);
		idata = mock(IData.class);
		pipelineMatcher = mock(Matcher.class);
	}
	
	@Test
	public void shouldBeApplicable() {

		when(serviceNameMatcher.match(pipelinePosition)).thenReturn(MatchResult.TRUE);
		when(pipelineMatcher.match(idata)).thenReturn(MatchResult.TRUE);
		ServicePipelinePointCut sppc = new ServicePipelinePointCut(serviceNameMatcher, pipelineMatcher, InterceptPoint.INVOKE);
		assertTrue(sppc.isApplicable(pipelinePosition, idata));
		
		assertEquals(InterceptPoint.INVOKE.toString(), sppc.toMap().get("interceptPoint"));
	}

	@Test
	public void shouldNotBeApplicable() {
		FlowPosition falsePipelinePosition = mock(FlowPosition.class);
		IData falseIdataMock = mock(IData.class);

		when(serviceNameMatcher.match(pipelinePosition)).thenReturn(MatchResult.TRUE);
		when(serviceNameMatcher.match(falsePipelinePosition)).thenReturn(MatchResult.FALSE);
		when(pipelineMatcher.match(idata)).thenReturn(MatchResult.TRUE);
		when(pipelineMatcher.match(falseIdataMock)).thenReturn(MatchResult.FALSE);

		ServicePipelinePointCut sppc = new ServicePipelinePointCut(serviceNameMatcher, pipelineMatcher, InterceptPoint.INVOKE);

		assertFalse(sppc.isApplicable(falsePipelinePosition, idata));
		assertFalse(sppc.isApplicable(pipelinePosition, falseIdataMock));
		assertTrue(sppc.isApplicable(pipelinePosition, idata));
		
		assertEquals(pipelineMatcher, sppc.getPipelineMatcher());
		assertEquals(serviceNameMatcher, sppc.getFlowPositionMatcher());
	}
}
