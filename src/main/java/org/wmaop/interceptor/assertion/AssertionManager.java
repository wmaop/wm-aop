package org.wmaop.interceptor.assertion;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AssertionManager  {

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