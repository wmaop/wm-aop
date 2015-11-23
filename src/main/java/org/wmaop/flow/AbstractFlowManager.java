package org.wmaop.flow;

import java.text.MessageFormat;

import org.apache.commons.lang.ArrayUtils;
import org.wmaop.aop.advice.Advice;
import org.wmaop.aop.chainprocessor.AOPChainProcessor;
import org.wmaop.aop.chainprocessor.Interceptor;
import org.wmaop.aop.matcher.AlwaysTrueMatcher;
import org.wmaop.aop.matcher.FlowPositionMatcherImpl;
import org.wmaop.aop.matcher.Matcher;
import org.wmaop.aop.matcher.jexl.JexlIDataMatcher;
import org.wmaop.aop.pipeline.FlowPosition;
import org.wmaop.aop.pointcut.InterceptPoint;
import org.wmaop.aop.pointcut.PointCut;
import org.wmaop.aop.pointcut.ServicePipelinePointCut;

import com.wm.app.b2b.server.ServiceException;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;

public abstract class AbstractFlowManager {

	public void mandatory(IData pipeline, String message, String... params) throws ServiceException {
		IDataCursor pipelineCursor = pipeline.getCursor();
		try {
			for (String p : params) {
				Object o = IDataUtil.get(pipelineCursor, p);
				if (o == null || "".equals(o)) {
					MessageFormat mf = new MessageFormat(message);
					throw new ServiceException(mf.format(ArrayUtils.addAll(new Object[]{p}, params)));
				}
			}
		} finally {
			pipelineCursor.destroy();
		}
	
	}

	@SafeVarargs
	public final <T> void oneof(String message, T input, T... values) throws ServiceException {
		for (T v : values) {
			if (v.equals(input)) {
				return;
			}
		}
		MessageFormat mf = new MessageFormat(message);
		throw new ServiceException(mf.format(ArrayUtils.addAll(new Object[]{input}, values)));
	}

	protected void registerInterceptor(String adviceId, String interceptPoint, String serviceName, String pipelineCondition, Interceptor interceptor) throws ServiceException {
		interceptPoint = interceptPoint.toUpperCase();
		oneof("interceptPoint {0} must be either {1}, {2} or {3}", interceptPoint, "BEFORE", "INVOKE", "AFTER");
		InterceptPoint ip = InterceptPoint.valueOf(interceptPoint);
	
		Matcher<FlowPosition> servicePositionMatcher = new FlowPositionMatcherImpl(serviceName, serviceName);
		Matcher<IData> pipelineMatcher;
		if (pipelineCondition != null && pipelineCondition.length() > 0) {
			pipelineMatcher = new JexlIDataMatcher(serviceName, pipelineCondition);
		} else {
			pipelineMatcher = new AlwaysTrueMatcher<IData>(serviceName);
		}
		PointCut joinPoint = new ServicePipelinePointCut(servicePositionMatcher, pipelineMatcher, ip);
		Advice advice = new Advice(adviceId, joinPoint, interceptor);
		AOPChainProcessor aop = AOPChainProcessor.getInstance();
		aop.registerAdvice(advice);
		aop.setEnabled(true);
	}
	
	protected void registerStub(String serviceName) throws ServiceException {
		AOPChainProcessor aop = AOPChainProcessor.getInstance();
		aop.getStubManager().registerStubService(serviceName);
	}
}
