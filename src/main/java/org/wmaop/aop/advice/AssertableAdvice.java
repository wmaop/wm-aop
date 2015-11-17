package org.wmaop.aop.advice;

import org.wmaop.interceptor.assertion.AssertionWrappingInterceptor;

public class AssertableAdvice extends Advice{

	public AssertableAdvice(Advice advice) {
		super(advice.getId(), advice.getPointCut(), new AssertionWrappingInterceptor(advice.getInterceptor(), advice.getId()));
		setAdviceState(advice.getAdviceState());
	}
}
