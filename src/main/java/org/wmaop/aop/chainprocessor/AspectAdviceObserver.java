package org.wmaop.aop.chainprocessor;

import java.util.Observable;
import java.util.Observer;

import org.wmaop.aop.advice.Advice;
import org.wmaop.interceptor.assertion.Assertable;
import org.wmaop.interceptor.bdd.BddInterceptor;

public class AspectAdviceObserver implements Observer {

	private StubManager stubManager;

	public AspectAdviceObserver(StubManager stubManager) {
		this.stubManager = stubManager;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		Advice advice = (Advice)arg;
		Interceptor interceptor = advice.getInterceptor();
		if (interceptor instanceof Assertable) {
			handleState(advice, (Assertable) interceptor);
		}
		if (interceptor instanceof BddInterceptor) {
			for (Interceptor icpt : ((BddInterceptor)interceptor).getInterceptorsOfType(Assertable.class)) {
				handleState(advice, (Assertable) icpt);
			}
		}
	}

	private void handleState(Advice advice, Assertable interceptor) {
		switch (advice.getAdviceState()) {
		case DISPOSED:
			stubManager.unregisterStub(advice);
			break;
		default:
			break;
		}
	}

}
