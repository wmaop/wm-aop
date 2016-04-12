package org.wmaop.aop.matcher;

import org.wmaop.aop.interceptor.FlowPosition;

public interface FlowPositionMatcher extends Matcher<FlowPosition> {

	String getServiceName();

}
