package org.wmaop.aop.matcher;

import org.wmaop.aop.pipeline.FlowPosition;

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
		return "FlowPositionMatcher["+serviceName+"]";
	}
	
}
