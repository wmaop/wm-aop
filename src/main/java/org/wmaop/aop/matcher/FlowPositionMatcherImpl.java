package org.wmaop.aop.matcher;

import org.wmaop.aop.pipeline.FlowPosition;

public class FlowPositionMatcherImpl implements FlowPositionMatcher {

	private final String serviceName;
	private final MatchResult matchTrue;

	public FlowPositionMatcherImpl(String id, String serviceName) {
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
		return "FlowPositionMatcherImpl["+serviceName+"]";
	}

	@Override
	public String getServiceName() {
		return serviceName;
	}
}
