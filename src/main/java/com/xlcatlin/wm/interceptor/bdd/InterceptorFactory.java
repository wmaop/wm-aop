package com.xlcatlin.wm.interceptor.bdd;

import java.io.IOException;

import com.xlcatlin.wm.aop.chainprocessor.Interceptor;
import com.xlcatlin.wm.interceptor.assertion.AssertionInterceptor;
import com.xlcatlin.wm.interceptor.bdd.xsd.Then;
import com.xlcatlin.wm.interceptor.mock.canned.CannedResponseInterceptor;
import com.xlcatlin.wm.interceptor.mock.exception.ExceptionInterceptor;
import com.xlcatlin.wm.interceptor.pipline.PipelineCaptureInterceptor;

public class InterceptorFactory {

	public Interceptor getInterceptor(Then then) {
		if (then.getAssert() != null) {
			return getAssertInterceptor(then.getReturn());
		} else if (then.getReturn() != null) {
			return getReturnInterceptor(then.getReturn());
		} else if (then.getPipelineCapture() != null) {
			return getPipelineCaptureInterceptor(then.getPipelineCapture());
		} else if (then.getThrow() != null) {
			return getExceptionInterceptor(then.getThrow());
		} else {
			throw new RuntimeException("No then actions");
		}
	}

	public Interceptor getReturnInterceptor(String content) {
		try {
			return new CannedResponseInterceptor(content);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Interceptor getPipelineCaptureInterceptor(String fileName) {
		return new PipelineCaptureInterceptor(fileName);
	}

	public Interceptor getExceptionInterceptor(String exc) {
		try {
			return new ExceptionInterceptor(exc);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public Interceptor getAssertInterceptor(String assertionName) {
		return new AssertionInterceptor(assertionName);
	}

}
