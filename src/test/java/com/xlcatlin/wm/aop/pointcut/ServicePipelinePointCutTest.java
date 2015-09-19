package com.xlcatlin.wm.aop.pointcut;

import static org.junit.Assert.*;

import org.junit.Test;

import static org.mockito.Mockito.*;

import com.wm.data.IData;
import com.xlcatlin.wm.aop.matcher.MatchResult;
import com.xlcatlin.wm.aop.matcher.Matcher;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;
import com.xlcatlin.wm.aop.pointcut.ServicePipelinePointCut;

public class ServicePipelinePointCutTest {

	@Test
	public void shouldBeApplicable() {
		FlowPosition pipelinePosition = mock(FlowPosition.class);
		Matcher<FlowPosition> serviceNameMatcher = mock(Matcher.class);
		Matcher<? super IData> pipelineMatcher = mock(Matcher.class);
		IData idataMock = mock(IData.class);
		
		when (serviceNameMatcher.match(pipelinePosition)).thenReturn(MatchResult.TRUE);
		when (pipelineMatcher.match(idataMock)).thenReturn(MatchResult.TRUE);
		ServicePipelinePointCut sppc = new ServicePipelinePointCut(serviceNameMatcher, pipelineMatcher);
		assertTrue(sppc.isApplicable(pipelinePosition, idataMock));
	}

	@Test
	public void shouldNotBeApplicable() {
		FlowPosition pipelinePosition = mock(FlowPosition.class);
		FlowPosition falsePipelinePosition = mock(FlowPosition.class);
		Matcher<FlowPosition> serviceNameMatcher = mock(Matcher.class);
		Matcher<? super IData> pipelineMatcher = mock(Matcher.class);
		IData idataMock = mock(IData.class);
		IData falseIdataMock = mock(IData.class);
		
		when (serviceNameMatcher.match(pipelinePosition)).thenReturn(MatchResult.TRUE);
		when (serviceNameMatcher.match(falsePipelinePosition)).thenReturn(MatchResult.FALSE);
		when (pipelineMatcher.match(idataMock)).thenReturn(MatchResult.TRUE);
		when (pipelineMatcher.match(falseIdataMock)).thenReturn(MatchResult.FALSE);
		
		ServicePipelinePointCut sppc = new ServicePipelinePointCut(serviceNameMatcher, pipelineMatcher);
		
		assertFalse(sppc.isApplicable(falsePipelinePosition, idataMock));
		assertFalse(sppc.isApplicable(pipelinePosition, falseIdataMock));
		assertTrue(sppc.isApplicable(pipelinePosition, idataMock));
	}
}
