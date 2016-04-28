package org.wmaop.aop.pointcut;

import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptPoint;
import org.wmaop.aop.matcher.Matcher;

import com.wm.data.IData;

public class ServicePipelinePointCut implements PointCut {

	final Matcher<FlowPosition> flowPositionMatcher;
	final Matcher<? super IData> pipelineMatcher;
	private InterceptPoint interceptPoint;

	public ServicePipelinePointCut(Matcher<FlowPosition> flowPositionMatcher, Matcher<? super IData> pipelineMatcher, InterceptPoint interceptPoint) {
		this.flowPositionMatcher = flowPositionMatcher;
		this.pipelineMatcher = pipelineMatcher;
		this.interceptPoint = interceptPoint;
	}

	public boolean isApplicable(FlowPosition pipelinePosition, IData idata) {
		return flowPositionMatcher.match(pipelinePosition).isMatch() && pipelineMatcher.match(idata).isMatch();
	}

	public Matcher<FlowPosition> getFlowPositionMatcher() {
		return flowPositionMatcher;
	}

	public Matcher<? super IData> getPipelineMatcher() {
		return pipelineMatcher;
	}

	@Override
	public String toString() {
		return "ServicePipelinePointCut[" + flowPositionMatcher + " & " + pipelineMatcher + ']';
	}

	@Override
	public InterceptPoint getInterceptPoint() {
		return interceptPoint;
	}

}
