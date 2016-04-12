package org.wmaop.aop.pointcut;

import org.wmaop.aop.matcher.Matcher;
import org.wmaop.aop.pipeline.FlowPosition;

import com.wm.data.IData;

public interface PointCut {

	boolean isApplicable(FlowPosition pipelinePosition, IData idata);

	InterceptPoint getInterceptPoint();

	Matcher<FlowPosition> getFlowPositionMatcher();
}
