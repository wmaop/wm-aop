package org.wmaop.aop.chainprocessor;

import static org.wmaop.aop.advice.AdviceState.ENABLED;
import static org.wmaop.aop.pointcut.InterceptPoint.AFTER;
import static org.wmaop.aop.pointcut.InterceptPoint.BEFORE;
import static org.wmaop.aop.pointcut.InterceptPoint.INVOKE;

import java.util.Iterator;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.wmaop.aop.advice.Advice;
import org.wmaop.aop.advice.AdviceManager;
import org.wmaop.aop.pipeline.FlowPosition;
import org.wmaop.aop.stub.StubManager;
import org.wmaop.interceptor.assertion.AspectAssertionObserver;
import org.wmaop.interceptor.assertion.AssertionManager;

import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.invoke.InvokeChainProcessor;
import com.wm.app.b2b.server.invoke.ServiceStatus;
import com.wm.data.IData;
import com.wm.util.ServerException;

public class AOPChainProcessor implements InvokeChainProcessor {

	private static final Logger logger = Logger.getLogger(AOPChainProcessor.class);

	private static final String PFX = "]>]> ";

	private static AOPChainProcessor instance;

	private boolean interceptingEnabled = false;

	private final AssertionManager assertionManager = new AssertionManager();
	private final StubManager stubManager = new StubManager();
	private final AdviceManager adviceManager = new AdviceManager();
	
	public static AOPChainProcessor getInstance() {
		return instance;
	}

	public AOPChainProcessor() {
		logger.info(PFX + "Initialising " + this.getClass().getName());
		adviceManager.reset();
		AOPChainProcessor.instance = this;
	
		adviceManager.addObserver(new AspectAssertionObserver(assertionManager));
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
		if (serviceStatus.getException() != null) {
			return; // Exception in before to prevent execution of service/mock
		}
		
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
			for (Advice advice : adviceManager.getAdvicesForInterceptPoint(pos.getInterceptPoint())) {
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
		
		if (interceptResult.hasIntercepted()) {
			Exception e = interceptResult.getException();
			if (e != null) {
				serviceStatus.setException(e);
			}
			return true;
		}
		return false;
	}

	public void reset() {
		adviceManager.reset();
		stubManager.clearStubs();
		assertionManager.removeAssertions();
		setEnabled(false);
	}
	
	/*
	 * ************* Advice handling ************* 
	 */

	public AdviceManager getAdviceManager() {
		return adviceManager;
	}
	
	/*
	 * ************* Stub handling ************* 
	 */
	public StubManager getStubManager() {
		return stubManager;
	}
}
