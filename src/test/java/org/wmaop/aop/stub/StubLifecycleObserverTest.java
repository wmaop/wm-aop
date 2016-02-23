package org.wmaop.aop.stub;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.wmaop.aop.advice.Advice;
import org.wmaop.aop.advice.AdviceState;
import org.wmaop.aop.chainprocessor.Interceptor;
import org.wmaop.interceptor.assertion.Assertable;
import org.wmaop.interceptor.assertion.AssertionWrappingInterceptor;
import org.wmaop.interceptor.bdd.BddInterceptor;

public class StubLifecycleObserverTest {

	@Test
	public void shouldNotAttemptStubUnregister() {
		StubManager stubManager = mock(StubManager.class);
		StubLifecycleObserver slo = new StubLifecycleObserver(stubManager);
		Advice advice = mock(Advice.class);
		Interceptor interceptor = mock(Interceptor.class);
		when(advice.getInterceptor()).thenReturn(interceptor);
		when(advice.getAdviceState()).thenReturn(AdviceState.DISPOSED);
		slo.update(null, advice);
		verify(stubManager, times(0)).unregisterStub(advice);
	}

	@Test
	public void shouldStubUnregisterAssertable() {
		StubManager stubManager = mock(StubManager.class);
		StubLifecycleObserver slo = new StubLifecycleObserver(stubManager);
		Advice advice = mock(Advice.class);
		Interceptor interceptor = mock(AssertionWrappingInterceptor.class);
		when(advice.getInterceptor()).thenReturn(interceptor);
		when(advice.getAdviceState()).thenReturn(AdviceState.ENABLED);
		slo.update(null, advice);
		when(advice.getAdviceState()).thenReturn(AdviceState.DISPOSED);
		slo.update(null, advice);
		verify(stubManager, times(1)).unregisterStub(advice);
	}
	
	@Test
	public void shouldStubUnregisterBdd() {
		StubManager stubManager = mock(StubManager.class);
		StubLifecycleObserver slo = new StubLifecycleObserver(stubManager);
		Advice advice = mock(Advice.class);
		BddInterceptor interceptor = mock(BddInterceptor.class);
		List<Interceptor> interceptors = new ArrayList<Interceptor>();
		interceptors.add(mock(AssertionWrappingInterceptor.class));
		fail("verify how this is working with interceptor and not on advice as whole.  should unregister on interceptor");
		when(interceptor.getInterceptorsOfType(Assertable.class)).thenReturn(interceptors);
		when(advice.getInterceptor()).thenReturn(interceptor);
		when(advice.getAdviceState()).thenReturn(AdviceState.ENABLED);
		slo.update(null, advice);
		when(advice.getAdviceState()).thenReturn(AdviceState.DISPOSED);
		slo.update(null, advice);
		verify(stubManager, times(1)).unregisterStub(advice);
	}
}
