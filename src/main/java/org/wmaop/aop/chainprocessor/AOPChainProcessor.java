package org.wmaop.aop.chainprocessor;

import static org.wmaop.aop.advice.AdviceState.DISPOSED;
import static org.wmaop.aop.advice.AdviceState.ENABLED;
import static org.wmaop.aop.advice.AdviceState.NEW;
import static org.wmaop.aop.pointcut.InterceptPoint.AFTER;
import static org.wmaop.aop.pointcut.InterceptPoint.BEFORE;
import static org.wmaop.aop.pointcut.InterceptPoint.INVOKE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.wmaop.aop.advice.Advice;
import org.wmaop.aop.advice.AssertableAdvice;
import org.wmaop.aop.matcher.FlowPositionMatcher;
import org.wmaop.aop.matcher.Matcher;
import org.wmaop.aop.pipeline.FlowPosition;
import org.wmaop.aop.pointcut.InterceptPoint;
import org.wmaop.interceptor.assertion.AspectAssertionObserver;
import org.wmaop.interceptor.assertion.Assertable;
import org.wmaop.interceptor.assertion.AssertionManager;
import org.wmaop.interceptor.bdd.BddInterceptor;

import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.ServiceException;
import com.wm.app.b2b.server.invoke.InvokeChainProcessor;
import com.wm.app.b2b.server.invoke.ServiceStatus;
import com.wm.data.IData;
import com.wm.lang.ns.NSException;
import com.wm.util.ServerException;

public class AOPChainProcessor extends Observable implements InvokeChainProcessor {

	private static final Logger logger = Logger.getLogger(AOPChainProcessor.class);

	private static final String PFX = "]>]> ";

	private static AOPChainProcessor instance;

	private final Map<InterceptPoint, List<Advice>> ADVICES = new HashMap<InterceptPoint, List<Advice>>();
	private final Map<String, Advice> ID_ADVICE = new HashMap<String, Advice>();
	private boolean interceptingEnabled = false;

	AssertionManager assertionManager = new AssertionManager();
	StubManager stubManager = new StubManager();
	
	public static AOPChainProcessor getInstance() {
		return instance;
	}

	public AOPChainProcessor() {
		logger.info(PFX + "Initialising " + this.getClass().getName());
		ADVICES.clear();
		ID_ADVICE.clear();
		for (InterceptPoint ip : InterceptPoint.values()) {
			ADVICES.put(ip, new ArrayList<Advice>());
		}
		AOPChainProcessor.instance = this;
	
		// This should be registered elsewhere though no other point of instantiation
		addObserver(new AspectAssertionObserver(assertionManager));
	}

	public AssertionManager getAssertionManager() {
		return assertionManager;
	}
	
	public void setEnabled(boolean enabled) {
		interceptingEnabled = enabled;
		logger.info(PFX + "Intercepting " + (enabled ? "enabled" : "disabled"));
	}

	public boolean isEnabled() {
		return interceptingEnabled;
	}

	/*
	 * ************* Interception ************* 
	 */
	
	@Override
	public void process(@SuppressWarnings("rawtypes") Iterator processorChain, BaseService baseService, IData idata,
			ServiceStatus serviceStatus) throws ServerException {

		if (interceptingEnabled) {
			processIntercept(processorChain, baseService, idata, serviceStatus);
		} else if (processorChain.hasNext()) {
			((InvokeChainProcessor) processorChain.next()).process(processorChain, baseService, idata, serviceStatus);
		}
	}

	private void processIntercept(@SuppressWarnings("rawtypes") Iterator processorChain, BaseService baseService,
			IData idata, ServiceStatus serviceStatus) throws ServerException {
		FlowPosition pipelinePosition = new FlowPosition(BEFORE, baseService.getNSName().getFullName());
		processAdvice(false, pipelinePosition, idata, serviceStatus);

		pipelinePosition.setInterceptPoint(INVOKE);
		boolean hasIntercepted = processAdvice(true, pipelinePosition, idata, serviceStatus);

		if (hasIntercepted && logger.isDebugEnabled()) {
			logger.debug("Intercepted: " + ReflectionToStringBuilder.toString(serviceStatus));
		}

		if (!hasIntercepted && processorChain.hasNext()) {
			((InvokeChainProcessor) processorChain.next()).process(processorChain, baseService, idata, serviceStatus);
		}

		pipelinePosition.setInterceptPoint(AFTER);
		processAdvice(false, pipelinePosition, idata, serviceStatus);
	}

	private boolean processAdvice(boolean exitOnIntercept, FlowPosition pos, IData idata, ServiceStatus serviceStatus) {
		boolean hasIntercepted = false;
		try {
			for (Advice advice : ADVICES.get(pos.getInterceptPoint())) {
				if (advice.getAdviceState() == ENABLED && advice.getPointCut().isApplicable(pos, idata)) {
					hasIntercepted = intercept(exitOnIntercept, pos, idata, serviceStatus, advice);
					if (hasIntercepted) {
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error(PFX + "Error intercepting. Behaviour at " + pos + " may be unknown", e);
		}
		return hasIntercepted;
	}

	private boolean intercept(boolean exitOnIntercept, FlowPosition pos, IData idata, ServiceStatus serviceStatus,
			Advice advice) {
		Interceptor interceptor = advice.getInterceptor();
		InterceptResult interceptResult = interceptor.intercept(pos, idata);
		logger.info(PFX + "Intercepting " + advice.getId() + " " + pos.getInterceptPoint() + ' ' + pos + " - " + interceptResult.hasIntercepted());
		
		if (interceptResult.hasIntercepted() && exitOnIntercept) {
			Exception e = interceptResult.getException();
			if (e != null) {
				serviceStatus.setException(e);
			}
			return true;
		}
		return false;
	}

	/*
	 * ************* Advice handling ************* 
	 */
	
	public void registerAdvice(Advice advice) {
		if (!(advice.getInterceptor() instanceof Assertable || advice.getInterceptor() instanceof BddInterceptor)) {
			advice = new AssertableAdvice(advice);
		}
		ADVICES.get(advice.getPointCut().getInterceptPoint()).add(advice);
		ID_ADVICE.put(advice.getId(), advice);
		// Notify if new
		if (advice.getAdviceState() == NEW) {
			setChanged();
			notifyObservers(advice);
		}
		advice.setAdviceState(ENABLED);
		setChanged();
		notifyObservers(advice);
		logger.info(PFX + "Registered advice " + advice);
	}

	public void unregisterAdvice(String adviceId) {
		unregisterAdvice(getAdvice(adviceId));
	}

	public void unregisterAdvice(Advice advice) {
		// Possibly wrapped so base the removal on the id
		List<Advice> advcs = ADVICES.get(advice.getPointCut().getInterceptPoint());
		for (Advice advc : advcs) {
			if (advc.getId().equals(advice.getId())) {
				advcs.remove(advc);
				break;
			}
		}
		ID_ADVICE.remove(advice.getId());
		advice.setAdviceState(DISPOSED);
		setChanged();
		notifyObservers(advice);
		stubManager.unregisterStub(advice);
	}

	public void clearAdvice() {
		for (List<Advice> advs : ADVICES.values()) {
			List<Advice> advCopy = new ArrayList<>(advs);
			for (Advice adv : advCopy) {
				unregisterAdvice(adv);
			}
		}
		logger.info(PFX + "Cleared all Advice");
		stubManager.clearStubs();
	}

	public Advice getAdvice(String id) {
		return ID_ADVICE.get(id);
	}

	public List<Advice> listAdvice() {
		List<Advice> list = new ArrayList<Advice>();
		for (List<Advice> adv : ADVICES.values()) {
			list.addAll(adv);
		}
		return list;
	}
	
	/*
	 * ************* Stub handling ************* 
	 */
	public StubManager getStubManager() {
		return stubManager;
	}
}
