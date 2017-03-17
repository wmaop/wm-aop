package org.wmaop.aop.advice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.wmaop.aop.advice.remit.GlobalRemit;
import org.wmaop.aop.advice.remit.Remit;
import org.wmaop.aop.advice.remit.UserRemit;
import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.Interceptor;
import org.wmaop.aop.pointcut.PointCut;

import com.wm.data.IData;
import com.wm.data.IDataFactory;

public class AdviceTest {

	private static final String ADVICE_ID = "adviceid";
	private PointCut pointCut;
	private Interceptor interceptor;
	private FlowPosition pipelinePosition;
	private IData idata;
	private Advice advice;
	private Remit remit;


	@Before
	public void setUp() {
		pointCut = mock(PointCut.class);
		interceptor = mock(Interceptor.class);
		pipelinePosition = mock(FlowPosition.class);
		idata = IDataFactory.create();
		remit = new GlobalRemit();
		advice = new Advice(ADVICE_ID, remit, pointCut, interceptor);
	}
	
	@Test
	public void shouldNotBeApplicableWhenPositionNotApplicable() {
		when(pointCut.isApplicable(pipelinePosition, idata)).thenReturn(false);
		assertFalse(advice.isApplicable(pipelinePosition, idata));
	}
	
	@Test
	public void shouldBeApplicableForPipelinePosition() {
		when(pointCut.isApplicable(pipelinePosition, idata)).thenReturn(true);
		assertTrue(advice.isApplicable(pipelinePosition, idata));
	}
	
	@Test
	public void shouldNotBeApplicableForOutOfScopeRemit() {
		advice = new Advice(ADVICE_ID, new UserRemit("Foo"), pointCut, interceptor);
		when(pointCut.isApplicable(pipelinePosition, idata)).thenReturn(true);
		assertFalse(advice.isApplicable(pipelinePosition, idata));
	}

	@Test
	public void shouldReportState() {
		assertEquals(ADVICE_ID, advice.getId());
		assertEquals(interceptor, advice.getInterceptor());
		assertEquals(pointCut, advice.getPointCut());
		assertEquals(remit, advice.getRemit());
		assertEquals(ADVICE_ID, advice.toMap().get("adviceId"));
	}

	@Test
	public void shouldReportAdviceStateChange() {
		assertEquals(AdviceState.NEW, advice.getAdviceState());
		advice.setAdviceState(AdviceState.ENABLED);
		assertEquals(AdviceState.ENABLED, advice.getAdviceState());
	}
}
