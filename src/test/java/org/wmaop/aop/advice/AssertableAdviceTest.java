package org.wmaop.aop.advice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.wmaop.aop.interceptor.AssertableInterceptor;
import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.Interceptor;
import org.wmaop.aop.pointcut.PointCut;

import com.wm.data.IData;
import com.wm.data.IDataFactory;

public class AssertableAdviceTest {

	@Test
	public void test() {
		
		PointCut pointCut = mock(PointCut.class);
		Interceptor interceptor = mock(Interceptor.class);
		Advice advice = new Advice("id", pointCut, interceptor);
		advice.setAdviceState(AdviceState.ENABLED);
		AssertableAdvice assertable = new AssertableAdvice(advice );
		
		assertEquals("id", assertable.getId());
		assertEquals(pointCut, assertable.getPointCut());
		assertTrue(assertable.getInterceptor() instanceof AssertableInterceptor);
		assertEquals(AdviceState.ENABLED, assertable.getAdviceState());
		
		FlowPosition flowPosition = mock(FlowPosition.class);
		IData idata = IDataFactory.create();
		Interceptor assertingInterceptor = assertable.getInterceptor();
		assertingInterceptor.intercept(flowPosition, idata);
		assertEquals(1, ((AssertableInterceptor) assertingInterceptor).getInvokeCount()); 

		
	}

}
