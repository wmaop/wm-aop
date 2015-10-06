package org.wmaop.interceptor.assertion;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AssertionManager  {

	private final Map<String, Assertion> assertions = new HashMap<String, Assertion>();
	
	public AssertionManager() {}
	
	public void addAssertion(String name, Assertion assertion) {
		assertions.put(name, assertion);
	}

	public Assertion getAssertion(String name) {
		return assertions.get(name);
	}
	
	public Collection<Assertion> getAssertions() {
		return assertions.values();
	}

	public Collection<String> getAssertionNames() {
		return assertions.keySet();
	}

	public int getInvokeCount(String name) {
		Assertion assertion = assertions.get(name);
		return assertion == null ? 0 : assertion.getInvokeCount();
	}
	
	public void removeAssertion(String name) {
		assertions.remove(name);
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