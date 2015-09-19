package com.xlcatlin.wm.aop.matcher;

import com.xlcatlin.wm.aop.pipeline.FlowPosition;

public class FlowPositionMatcher implements Matcher<FlowPosition> {

	private final String serviceName;
	private final MatchResult matchTrue;

	public FlowPositionMatcher(String id, String serviceName) {
		this.serviceName = serviceName;
		matchTrue = new MatchResult(true, id);
	}

	public MatchResult match(FlowPosition obj) {
		if (obj == null || !serviceName.equals(obj.toString()))
			return MatchResult.FALSE;
		return matchTrue;
	}

	@Override
	public String toString() {
		return "FlowPosiionMatcher["+serviceName+"]";
	}
	
}
