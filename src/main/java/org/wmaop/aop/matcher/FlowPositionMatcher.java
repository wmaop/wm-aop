package org.wmaop.aop.matcher;

import org.wmaop.aop.pipeline.FlowPosition;

public interface FlowPositionMatcher extends Matcher<FlowPosition> {

	String getServiceName();

}
