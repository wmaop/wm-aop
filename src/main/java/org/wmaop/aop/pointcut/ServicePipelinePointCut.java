package org.wmaop.aop.pointcut;

import org.wmaop.aop.matcher.Matcher;
import org.wmaop.aop.pipeline.FlowPosition;

import com.wm.data.IData;

public class ServicePipelinePointCut implements PointCut {

	final Matcher<FlowPosition> serviceNameMatcher;
	final Matcher<? super IData> pipelineMatcher;
	private InterceptPoint interceptPoint;

	public ServicePipelinePointCut(Matcher<FlowPosition> matcher, Matcher<? super IData> pipelineMatcher, InterceptPoint interceptPoint) {
		this.serviceNameMatcher = matcher;
		this.pipelineMatcher = pipelineMatcher;
		this.interceptPoint = interceptPoint;
	}

	public boolean isApplicable(FlowPosition pipelinePosition, IData idata) {
		return serviceNameMatcher.match(pipelinePosition).isMatch() && pipelineMatcher.match(idata).isMatch();
	}

	public Matcher<FlowPosition> getServiceNameMatcher() {
		return serviceNameMatcher;
	}

	public Matcher<? super IData> getPipelineMatcher() {
		return pipelineMatcher;
	}

	@Override
	public String toString() {
		return "ServicePipelinePointCut[" + serviceNameMatcher + " & " + pipelineMatcher + ']';
	}

	@Override
	public InterceptPoint getInterceptPoint() {
		return interceptPoint;
	}

}
