package org.wmaop.interceptor.assertion;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Observable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wmaop.aop.advice.Advice;
import org.wmaop.aop.advice.AdviceState;

@RunWith(PowerMockRunner.class)
public class AspectAssertionObserverTest {

	@Test
	public void test() {
		AssertionManager asm = mock(AssertionManager.class);

		AssertionInterceptor ai = new AssertionInterceptor("foo assertion");
		Advice advice = mock(Advice.class);
		when(advice.getInterceptor()).thenReturn(ai);
		when(advice.getAdviceState()).thenReturn(AdviceState.NEW);
		
		Observable observable = new Observable(){};
		
		AspectAssertionObserver aao = new AspectAssertionObserver(asm);
		aao.update(observable, advice);
		
		verify(asm, times(1)).addAssertion("foo assertion", ai);
		verify(asm, times(0)).removeAssertion("foo assertion");
	
		when(advice.getAdviceState()).thenReturn(AdviceState.ENABLED);
		verify(asm, times(1)).addAssertion("foo assertion", ai);
		verify(asm, times(0)).removeAssertion("foo assertion");
		
		
		when(advice.getAdviceState()).thenReturn(AdviceState.DISPOSED);
		aao.update(observable, advice);
		verify(asm, times(1)).addAssertion("foo assertion", ai);
		verify(asm, times(1)).removeAssertion("foo assertion");
	

	}

}
