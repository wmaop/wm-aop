package com.xlcatlin.wm.interceptor.assertion;

public class Verify {

	public static boolean onceOnly(AssertionInterceptor assertion) {
		return assertion.getInvokeCount() == 1 && assertion.hasAsserted();
	}
	
	public static boolean atLeastOnce(AssertionInterceptor assertion) {
		return assertion.getInvokeCount() > 1 && assertion.hasAsserted();
	}
	
	public static boolean atLeast(int count, AssertionInterceptor assertion) {
		return assertion.getInvokeCount() > count && assertion.hasAsserted();
	}
	
	public static boolean never(AssertionInterceptor assertion) {
		return assertion.getInvokeCount() == 0;
	}

	public static boolean atMost(int count, AssertionInterceptor assertion) {
		return assertion.getInvokeCount() <= count && assertion.hasAsserted();
	}
	
}
