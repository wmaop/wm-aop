package org.wmaop.interceptor.assertion;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.wmaop.aop.interceptor.AssertableInterceptor;

public class AssertionManager  {

	private static final Logger logger = Logger.getLogger(AssertionManager.class);
	private final Map<String, AssertableInterceptor> assertions = new HashMap<String, AssertableInterceptor>();
	
	public AssertionManager() {}
	
	public void addAssertion(String name, AssertableInterceptor assertion) {
		assertions.put(name, assertion);
	}

	public AssertableInterceptor getAssertion(String name) {
		return assertions.get(name);
	}
	
	public Collection<AssertableInterceptor> getAssertions() {
		return assertions.values();
	}

	public Collection<String> getAssertionNames() {
		return assertions.keySet();
	}

	public int getInvokeCount(String name) {
		AssertableInterceptor assertion = assertions.get(name);
		return assertion == null ? 0 : assertion.getInvokeCount();
	}
	
	public int getInvokeCountForPrefix(String prefix) {
		int invokeCount = 0;
		Collection <AssertableInterceptor> assertables = new HashSet<>();
		for (Entry<String, AssertableInterceptor> e : assertions.entrySet()) {
			if (e.getKey().startsWith(prefix)) {
				assertables.add(e.getValue());
			}
		}
		if (assertables.isEmpty()) {
			logger.warn("]>]> ** No assertion found for prefix " + prefix);
		} else {
			for (AssertableInterceptor a : assertables) {
				invokeCount += a.getInvokeCount();
			}
		}
		return invokeCount;
	}
	
	public void removeAssertion(String name) {
		assertions.remove(name);
	}

	public void removeAssertions() {
		assertions.clear();
	}
	
	public boolean verifyOnceOnly(String name) {
		return getAssertion(name).getInvokeCount() == 1;
	}
	
	public boolean verifyAtLeastOnce(String name) {
		return getAssertion(name).getInvokeCount() >= 1;
	}
	
	public boolean verifyAtLeast(int count, String name) {
		return getAssertion(name).getInvokeCount() >= count;
	}
	
	public boolean verifyNever(String name) {
		return getAssertion(name).getInvokeCount() == 0;
	}

	public boolean verifyAtMost(int count, String name) {
		return getAssertion(name).getInvokeCount() <= count;
	}

}