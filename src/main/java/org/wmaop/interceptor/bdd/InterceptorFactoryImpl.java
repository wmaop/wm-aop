package org.wmaop.interceptor.bdd;

import java.io.IOException;
import java.util.List;

import org.wmaop.aop.assertion.AssertionInterceptor;
import org.wmaop.aop.interceptor.InterceptionException;
import org.wmaop.aop.interceptor.Interceptor;
import org.wmaop.interceptor.bdd.xsd.Assert;
import org.wmaop.interceptor.bdd.xsd.Then;
import org.wmaop.interceptor.delegating.FlowServiceDelegatingInterceptor;
import org.wmaop.interceptor.mock.canned.CannedResponseInterceptor;
import org.wmaop.interceptor.mock.canned.CannedResponseInterceptor.ResponseSequence;
import org.wmaop.interceptor.mock.exception.ExceptionInterceptor;
import org.wmaop.interceptor.pipline.PipelineCaptureInterceptor;

public class InterceptorFactoryImpl implements InterceptorFactory {

	@Override
	public Interceptor getInterceptor(Then then) {
		if (then.getAssert() != null) {
			return getAssertInterceptor(then.getAssert());
		} else if (then.getReturn() != null && !then.getReturn().isEmpty()) {
			return getReturnInterceptor(then.getReturn());
		} else if (then.getPipelineCapture() != null) {
			return getPipelineCaptureInterceptor(then.getPipelineCapture());
		} else if (then.getThrow() != null) {
			return getExceptionInterceptor(then.getThrow());
		} else if (then.getInvoke() != null) {
			return getFlowServiceDelegatingInterceptor(then.getInvoke());
		} else {
			throw new InterceptionException("No then actions within the scenario");
		}
	}

	private Interceptor getFlowServiceDelegatingInterceptor(String fqServiceName) {
		return new FlowServiceDelegatingInterceptor(fqServiceName);
	}

	public Interceptor getReturnInterceptor(List<String> list) {
		try {
			return new CannedResponseInterceptor(ResponseSequence.SEQUENTIAL, list);
		} catch (IOException e) {
			throw new InterceptionException("Error while decoding IData for CannedResponse - Format issue with IDATA?", e);
		}
	}

	public Interceptor getPipelineCaptureInterceptor(String fileName) {
		return new PipelineCaptureInterceptor(fileName);
	}

	public Interceptor getExceptionInterceptor(String exceptionClassName) {
		try {
			return new ExceptionInterceptor(exceptionClassName);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new InterceptionException("Problem resolving exception class " + exceptionClassName, e);
		}
	}

	public Interceptor getAssertInterceptor(Assert ass) {
		return new AssertionInterceptor(ass.getId());
	}

}
