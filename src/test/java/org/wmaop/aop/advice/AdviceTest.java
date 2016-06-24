package org.wmaop.aop.advice;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.wmaop.aop.advice.remit.GlobalRemit;
import org.wmaop.aop.advice.remit.UserRemit;
import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.Interceptor;
import org.wmaop.aop.pointcut.PointCut;

import com.wm.data.IData;
import com.wm.data.IDataFactory;

public class AdviceTest {

	@Test
	public void testApplicable() {
		PointCut pointCut = mock(PointCut.class);
		Interceptor interceptor = mock(Interceptor.class);
		Advice advice = new Advice("id", new GlobalRemit(), pointCut, interceptor);
		
		IData idata = IDataFactory.create();
		FlowPosition pipelinePosition = mock(FlowPosition.class);
		advice.isApplicable(pipelinePosition , idata);
		
		when(pointCut.isApplicable(pipelinePosition, idata)).thenReturn(false);
		assertFalse(advice.isApplicable(pipelinePosition, idata));

		when(pointCut.isApplicable(pipelinePosition, idata)).thenReturn(true);
		assertTrue(advice.isApplicable(pipelinePosition, idata));
		
		advice = new Advice("id", new UserRemit("Foo"), pointCut, interceptor);
		when(pointCut.isApplicable(pipelinePosition, idata)).thenReturn(true);
		assertFalse(advice.isApplicable(pipelinePosition, idata));
	}

	@Test
	public void testOther() {
		PointCut pointCut = mock(PointCut.class);
		Interceptor interceptor = mock(Interceptor.class);
		Advice advice = new Advice("id", new GlobalRemit(), pointCut, interceptor);
		assertEquals("id", advice.toMap().get("adviceId"));
	}
}
