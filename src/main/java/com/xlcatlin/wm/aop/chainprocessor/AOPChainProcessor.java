package com.xlcatlin.wm.aop.chainprocessor;

import static com.xlcatlin.wm.aop.InterceptPoint.AFTER;
import static com.xlcatlin.wm.aop.InterceptPoint.BEFORE;
import static com.xlcatlin.wm.aop.InterceptPoint.INVOKE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.invoke.InvokeChainProcessor;
import com.wm.app.b2b.server.invoke.ServiceStatus;
import com.wm.data.IData;
import com.wm.util.ServerException;
import com.xlcatlin.wm.aop.Advice;
import com.xlcatlin.wm.aop.InterceptPoint;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

public class AOPChainProcessor implements InvokeChainProcessor {

	private static AOPChainProcessor instance;
	
	private final Map<InterceptPoint, List<Advice>> ADVICES = new HashMap<InterceptPoint, List<Advice>>();
	private final Map<String, Advice> ID_ADVICE = new HashMap<String, Advice>();
	private boolean interceptingEnabled = false;

	public static AOPChainProcessor getInstance() {
		return instance;
	}

	public AOPChainProcessor() {
		System.out.println("]>]> Initialising " + this.getClass().getName());
		for (InterceptPoint ip : InterceptPoint.values()) {
			ADVICES.put(ip, new ArrayList<Advice>());
		}
		AOPChainProcessor.instance = this;
	}

	public void clearAdvice() {
		for (List<Advice> adv: ADVICES.values()) {
			adv.clear();
		}
		ID_ADVICE.clear();
		System.out.println("]>]> Cleared all Advice");
	}

	public void registerAdvice(Advice advice) {
		ADVICES.get(advice.getInterceptPoint()).add(advice);
		ID_ADVICE.put(advice.getId(), advice);
		System.out.println("]>]> Registered advice " + advice);
	}

	public void unregisterAdvice(String adviceId) {
		ID_ADVICE.remove(adviceId);
	}
	
	public void unregisterAdvice(Advice advice) {
		ADVICES.get(advice.getInterceptPoint()).remove(advice);
		ID_ADVICE.remove(advice).getId();
	}

	public Advice getAdvice(String id) {
		return ID_ADVICE.get(id);
	}
	
	public List<Advice> listAdvice() {
		List<Advice> list = new ArrayList<Advice>();
		for (List<Advice> adv: ADVICES.values()) {
			list.addAll(adv);
		}
		return list;
	}

	public void setEnabled(boolean enabled) {
		interceptingEnabled = enabled;
		System.out.println("]>]> Intercepting " + (enabled?"enabled":"disabled"));
	}

	public boolean isEnabled() {
		return interceptingEnabled;
	}
	
	public void process(@SuppressWarnings("rawtypes") Iterator processorChain,
			BaseService baseService, IData idata, ServiceStatus serviceStatus)
			throws ServerException {
		
		if (interceptingEnabled) {
			processIntercept(processorChain, baseService, idata, serviceStatus);
		} else if (processorChain.hasNext()) {
			((InvokeChainProcessor) processorChain.next()).process(
					processorChain, baseService, idata, serviceStatus);
		}
	}

	private void processIntercept(@SuppressWarnings("rawtypes") Iterator processorChain,
			BaseService baseService, IData idata, ServiceStatus serviceStatus)
			throws ServerException {
		FlowPosition pipelinePosition = new FlowPosition(BEFORE, baseService.getNSName().getFullName());
		processAdvice(false, pipelinePosition, idata, serviceStatus);
		
		pipelinePosition.setInterceptPoint(INVOKE);
		boolean hasIntercepted = processAdvice(true, pipelinePosition, idata, serviceStatus);
		
		System.out.println(ReflectionToStringBuilder.toString(serviceStatus));
		
		if (!hasIntercepted && processorChain.hasNext()) {
			((InvokeChainProcessor) processorChain.next()).process(
					processorChain, baseService, idata, serviceStatus);
		}
		
		pipelinePosition.setInterceptPoint(AFTER);
		processAdvice(false, pipelinePosition, idata, serviceStatus);
	}

	private boolean processAdvice(boolean exitOnIntercept, FlowPosition pos, IData idata, ServiceStatus serviceStatus) {
		for (Advice advice : ADVICES.get(pos.getInterceptPoint())) {
			if (advice.getPointCut().isApplicable(pos, idata)) {
				Interceptor interceptor = advice.getInterceptor();
				System.out.println("]>]> Intercepting " + pos);
				InterceptResult interceptResult = interceptor.intercept(pos, idata);
				if (interceptResult.hasIntercepted() && exitOnIntercept) {
					Exception e = interceptResult.getException();
					if (e != null) {
						serviceStatus.setException(e);
					}
					return true;
				}
			}
		}
		return false;
	}
}
