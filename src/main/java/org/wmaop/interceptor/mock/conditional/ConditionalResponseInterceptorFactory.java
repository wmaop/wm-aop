package org.wmaop.interceptor.mock.conditional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.wmaop.aop.Advice;
import org.wmaop.aop.InterceptPoint;
import org.wmaop.aop.PointCut;
import org.wmaop.aop.matcher.FlowPositionMatcher;
import org.wmaop.aop.matcher.Matcher;
import org.wmaop.aop.matcher.jexl.JexlIDataMatcher;
import org.wmaop.aop.matcher.jexl.JexlServiceNameMatcher;
import org.wmaop.aop.pipeline.FlowPosition;
import org.wmaop.aop.pointcut.ServicePipelinePointCut;
import org.wmaop.interceptor.mock.canned.CannedResponseInterceptor;

public class ConditionalResponseInterceptorFactory {

	public List<Advice> parseXML(String serviceName, InputStream responseXMLStream) {
		try {
			return parseXML(serviceName, new XMLResponseParser().parse(responseXMLStream));
		} catch (Exception e) {
			throw new RuntimeException("Unable to parse XML for " + serviceName, e);
		}
	}

	public List<Advice> parseXML(String serviceName, String responseXML) {
		try {
			return parseXML(serviceName, new XMLResponseParser().parse(responseXML));
		} catch (Exception e) {
			throw new RuntimeException("Unable to parse XML for " + serviceName, e);
		}
	}

	public List<Advice> parseXML(String serviceName, List<ConditionResponse> expressions) {
		List<Advice> advices = new ArrayList<Advice>();
		for (ConditionResponse pair : expressions) {
			String sid = pair.getId();
			PointCut pointCut = new ServicePipelinePointCut(getServiceNameMatcher(serviceName), new JexlIDataMatcher(sid, pair.getExpression()), InterceptPoint.INVOKE);
			try {
				advices.add(new Advice(sid, pointCut, new CannedResponseInterceptor(pair.getResponse())));
			} catch (Exception e) {
				throw new RuntimeException("Unable to parse XML for " + serviceName, e);
			}
		}
		return advices;
	}

	/**
	 * Return the matcher based on the text presented. Alpha numeric with : or _
	 * for a straight string match, any other for Jexl
	 */
	private Matcher<FlowPosition> getServiceNameMatcher(String serviceName) {
		for (char c : serviceName.toCharArray()) {
			if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == ':' || c == '_') {
				continue;
			} else {
				return new JexlServiceNameMatcher(serviceName, serviceName);
			}
		}
		return new FlowPositionMatcher(serviceName, serviceName);
	}

}