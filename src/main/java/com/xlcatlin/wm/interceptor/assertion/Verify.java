package com.xlcatlin.wm.interceptor.assertion;

public class Verify {

	public static boolean onceOnly(Assertion assertion) {
		return assertion.getInvokeCount() == 1 && assertion.hasAsserted();
	}
	
	public static boolean atLeastOnce(Assertion assertion) {
		return assertion.getInvokeCount() > 1 && assertion.hasAsserted();
	}
	
	public static boolean atLeast(int count, Assertion assertion) {
		return assertion.getInvokeCount() > count && assertion.hasAsserted();
	}
	
	public static boolean never(Assertion assertion) {
		return assertion.getInvokeCount() == 0;
	}

	public static boolean atMost(int count, Assertion assertion) {
		return assertion.getInvokeCount() <= count && assertion.hasAsserted();
	}
	
}
