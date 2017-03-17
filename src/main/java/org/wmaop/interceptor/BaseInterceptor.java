package org.wmaop.interceptor;

import java.util.HashMap;
import java.util.Map;

import org.wmaop.aop.interceptor.Interceptor;

public abstract class BaseInterceptor implements Interceptor {

	public static final String MAP_INVOKE_COUNT = "invokeCount";
	public static final String MAP_NAME = "name";
	public static final String MAP_TYPE = "type";
	
	protected final String name;
	protected int invokeCount = 0;

	protected BaseInterceptor(String name) {
		this.name = name;
	}
	
	@Override
	public int getInvokeCount() {
		return invokeCount;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> am = new HashMap<>();
		am.put(MAP_NAME, name);
		am.put(MAP_INVOKE_COUNT, invokeCount);
		addMap(am);
		return am;
	}

	protected abstract void addMap(Map<String, Object> am);
	
}
