package org.wmaop.aop.pointcut;

import java.util.HashMap;
import java.util.Map;

import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptPoint;
import org.wmaop.aop.matcher.FlowPositionMatcher;
import org.wmaop.aop.matcher.Matcher;

import com.wm.data.IData;

public class ServicePipelinePointCut implements PointCut {

	final FlowPositionMatcher flowPositionMatcher;
	final Matcher<? super IData> pipelineMatcher;
	private InterceptPoint interceptPoint;

	public ServicePipelinePointCut(FlowPositionMatcher flowPositionMatcher, Matcher<? super IData> pipelineMatcher, InterceptPoint interceptPoint) {
		this.flowPositionMatcher = flowPositionMatcher;
		this.pipelineMatcher = pipelineMatcher;
		this.interceptPoint = interceptPoint;
	}

	@Override
	public boolean isApplicable(FlowPosition pipelinePosition, IData idata) {
		return flowPositionMatcher.match(pipelinePosition).isMatch() && pipelineMatcher.match(idata).isMatch();
	}

	@Override
	public FlowPositionMatcher getFlowPositionMatcher() {
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

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> am = new HashMap<>();
		am.put("interceptPoint", interceptPoint.toString());
		am.put("flowPositionMatcher", flowPositionMatcher.toMap());
		am.put("pipelineMatcher", pipelineMatcher.toMap());
		return am;
	}

}
