package com.xlcatlin.wm.interceptor.assertion;

import java.util.Observable;
import java.util.Observer;

import com.xlcatlin.wm.aop.Advice;
import com.xlcatlin.wm.aop.chainprocessor.Interceptor;

public class AspectAssertionObserver implements Observer {

	@Override
	public void update(Observable o, Object arg) {
		Advice advice = (Advice)arg;
		Interceptor interceptor = advice.getInterceptor();
		if (interceptor instanceof Assertion) {
			switch (advice.getAdviceState()) {
			case NEW:
				AssertionManager.getInstance().addAssertion(advice.getId(), (Assertion) interceptor);
			case DISPOSED:
				AssertionManager.getInstance().removeAssertion(advice.getId());
			}
		}
	}

}
