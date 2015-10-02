package org.wmaop.interceptor.bdd;

import java.io.IOException;

import org.wmaop.aop.chainprocessor.Interceptor;
import org.wmaop.interceptor.assertion.AssertionInterceptor;
import org.wmaop.interceptor.mock.canned.CannedResponseInterceptor;
import org.wmaop.interceptor.mock.exception.ExceptionInterceptor;
import org.wmaop.interceptor.pipline.PipelineCaptureInterceptor;

import com.xlcatlin.wm.interceptor.bdd.xsd.Assert;
import com.xlcatlin.wm.interceptor.bdd.xsd.Then;

public class InterceptorFactory {

	public Interceptor getInterceptor(Then then) {
		if (then.getAssert() != null) {
			return getAssertInterceptor(then.getAssert());
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

	public Interceptor getAssertInterceptor(Assert ass) {
		return new AssertionInterceptor(ass.getId());
	}

}
