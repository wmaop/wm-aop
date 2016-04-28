package org.wmaop.aop.pointcut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptPoint;
import org.wmaop.aop.matcher.MatchResult;
import org.wmaop.aop.matcher.Matcher;

import com.wm.data.IData;

public class ServicePipelinePointCutTest {

	@SuppressWarnings("unchecked")
	@Test
	public void shouldBeApplicable() {
		FlowPosition pipelinePosition = mock(FlowPosition.class);
		Matcher<FlowPosition> serviceNameMatcher = mock(Matcher.class);
		Matcher<? super IData> pipelineMatcher = mock(Matcher.class);
		IData idataMock = mock(IData.class);

		when(serviceNameMatcher.match(pipelinePosition)).thenReturn(MatchResult.TRUE);
		when(pipelineMatcher.match(idataMock)).thenReturn(MatchResult.TRUE);
		ServicePipelinePointCut sppc = new ServicePipelinePointCut(serviceNameMatcher, pipelineMatcher, InterceptPoint.INVOKE);
		assertTrue(sppc.isApplicable(pipelinePosition, idataMock));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldNotBeApplicable() {
		FlowPosition pipelinePosition = mock(FlowPosition.class);
		FlowPosition falsePipelinePosition = mock(FlowPosition.class);
		Matcher<FlowPosition> serviceNameMatcher = mock(Matcher.class);
		Matcher<? super IData> pipelineMatcher = mock(Matcher.class);
		IData idataMock = mock(IData.class);
		IData falseIdataMock = mock(IData.class);

		when(serviceNameMatcher.match(pipelinePosition)).thenReturn(MatchResult.TRUE);
		when(serviceNameMatcher.match(falsePipelinePosition)).thenReturn(MatchResult.FALSE);
		when(pipelineMatcher.match(idataMock)).thenReturn(MatchResult.TRUE);
		when(pipelineMatcher.match(falseIdataMock)).thenReturn(MatchResult.FALSE);

		ServicePipelinePointCut sppc = new ServicePipelinePointCut(serviceNameMatcher, pipelineMatcher, InterceptPoint.INVOKE);

		assertFalse(sppc.isApplicable(falsePipelinePosition, idataMock));
		assertFalse(sppc.isApplicable(pipelinePosition, falseIdataMock));
		assertTrue(sppc.isApplicable(pipelinePosition, idataMock));
		
		assertEquals(pipelineMatcher, sppc.getPipelineMatcher());
		assertEquals(serviceNameMatcher, sppc.getFlowPositionMatcher());
	}
}
