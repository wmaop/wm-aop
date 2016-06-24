package org.wmaop.aop.matcher;

import java.util.HashMap;
import java.util.Map;

import org.wmaop.aop.interceptor.FlowPosition;

public class FlowPositionMatcherImpl implements FlowPositionMatcher {

	private final String serviceName;
	private final MatchResult matchTrue;
	private final String id;

	public FlowPositionMatcherImpl(String id, String serviceName) {
		this.serviceName = serviceName;
		this.id = id;
		matchTrue = new MatchResult(true, id);
	}

	@Override
	public MatchResult match(FlowPosition obj) {
		if (obj == null || !serviceName.equals(obj.toString()))
			return MatchResult.FALSE;
		return matchTrue;
	}

	@Override
	public String toString() {
		return "FlowPositionMatcherImpl["+serviceName+"]";
	}

	@Override
	public String getServiceName() {
		return serviceName;
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> am = new HashMap<>();
		am.put("id",  id);
		am.put("type", "FlowPositionMatcher");
		am.put("serviceName", serviceName);
		return am;
	}
}
