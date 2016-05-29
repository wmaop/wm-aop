package org.wmaop.aop.assertion;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class AssertionManager  {
/*
	private static final Logger logger = Logger.getLogger(AssertionManager.class);
	private final Map<String, Assertable> assertions = new HashMap<String, Assertable>();
	
	public AssertionManager() {}
	
	public void addAssertion(String name, Assertable assertion) {
		assertions.put(name, assertion);
	}

	public Assertable getAssertion(String name) {
		return assertions.get(name);
	}
	
	public Collection<Assertable> getAssertions() {
		return assertions.values();
	}

	public Collection<String> getAssertionNames() {
		return assertions.keySet();
	}

	public int getInvokeCount(String name) {
		Assertable assertion = assertions.get(name);
		return assertion == null ? 0 : assertion.getInvokeCount();
	}
	
	public int getInvokeCountForPrefix(String prefix) {
		int invokeCount = 0;
		Collection <Assertable> assertables = new HashSet<>();
		for (Entry<String, Assertable> e : assertions.entrySet()) {
			if (e.getKey().startsWith(prefix)) {
				assertables.add(e.getValue());
			}
		}
		if (assertables.isEmpty()) {
			logger.warn("]>]> ** No assertion found for prefix " + prefix);
		} else {
			for (Assertable a : assertables) {
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
*/
}