package com.xlcatlin.wm.interceptor.assertion;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AssertionManager  {

	private static AssertionManager instance = new AssertionManager();
	
	private final Map<String, Assertion> assertions = new HashMap<String, Assertion>();
	
	public static AssertionManager getInstance() {
		return instance;
	}
	
	protected AssertionManager() {}
	
	public void addAssertion(String name, Assertion assertion) {
		assertions.put(name, assertion);
	}

	public Assertion getAssetion(String name) {
		return assertions.get(name);
	}
	
	public Collection<Assertion> getAssetions() {
		return assertions.values();
	}

	public Collection<String> getAssetionNames() {
		return assertions.keySet();
	}

	public void removeAssertion(String name) {
		assertions.remove(name);
	}

}