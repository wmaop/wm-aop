package org.wmaop.aop.pointcut;

import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptPoint;
import org.wmaop.aop.matcher.FlowPositionMatcher;

import com.wm.data.IData;

public interface PointCut {

	boolean isApplicable(FlowPosition pipelinePosition, IData idata);

	InterceptPoint getInterceptPoint();

	FlowPositionMatcher getFlowPositionMatcher();

	Object toMap();
}
