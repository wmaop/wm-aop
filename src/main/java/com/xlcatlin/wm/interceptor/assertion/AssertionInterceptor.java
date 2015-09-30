package com.xlcatlin.wm.interceptor.assertion;

import com.wm.data.IData;
import com.xlcatlin.wm.aop.chainprocessor.InterceptResult;
import com.xlcatlin.wm.aop.chainprocessor.Interceptor;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

/**
 * Default assertion. Counts the invokes and registers if one
 *         has asserted
 */
public class AssertionInterceptor implements Interceptor, Assertion {

	private final String assertionName;
	private int invokeCount = 0;
	private boolean asserted;

	public boolean performAssert(IData idata) {
		return true;
	}

	public AssertionInterceptor(String assertionName) {
		this.assertionName = assertionName;
	}

	public void reset() {
		invokeCount = 0;
	}

	public final InterceptResult intercept(FlowPosition flowPosition, IData idata) {
		invokeCount++;
		if (performAssert(idata))
			asserted = true;
		return InterceptResult.FALSE;
	}

	public int getInvokeCount() {
		return invokeCount;
	}

	public boolean hasAsserted() {
		return asserted;
	}

	@Override
	public String getName() {
		return assertionName;
	}
}
