package org.wmaop.chainprocessor;

import static org.wmaop.aop.advice.AdviceState.ENABLED;
import static org.wmaop.aop.interceptor.InterceptPoint.AFTER;
import static org.wmaop.aop.interceptor.InterceptPoint.BEFORE;
import static org.wmaop.aop.interceptor.InterceptPoint.INVOKE;

import java.util.Iterator;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.wmaop.aop.advice.Advice;
import org.wmaop.aop.advice.AdviceManager;
import org.wmaop.aop.advice.Scope;
import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptResult;
import org.wmaop.aop.interceptor.Interceptor;
import org.wmaop.aop.stub.StubLifecycleObserver;
import org.wmaop.aop.stub.StubManager;
import org.wmaop.util.logger.Logger;

import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.invoke.InvokeChainProcessor;
import com.wm.app.b2b.server.invoke.ServiceStatus;
import com.wm.data.IData;
import com.wm.util.ServerException;

public class AOPChainProcessor implements InvokeChainProcessor {

	private static final InterceptResult NO_INTERCEPT = new InterceptResult(false);

	private static final Logger logger = Logger.getLogger(AOPChainProcessor.class);

	private static AOPChainProcessor instance;

	private boolean interceptingEnabled = false;

	private final StubManager stubManager;
	private final AdviceManager adviceManager;
	
	public static AOPChainProcessor getInstance() {
		return instance;
	}
	
	public static void setInstance(AOPChainProcessor acp) {
		instance = acp;
		
	}

	/**
	 * Instantiated by invokemanager - Limited control so no Spring here...
	 */
	public AOPChainProcessor() {
		this(new AdviceManager(), new StubManager());
	}

	public AOPChainProcessor(AdviceManager advMgr, StubManager stbMgr) {
		adviceManager = advMgr;
		stubManager = stbMgr;
		
		logger.info("Initialising " + this.getClass().getName());
		adviceManager.reset(Scope.ALL);
		setInstance(this);
	
		adviceManager.addObserver(new StubLifecycleObserver(stubManager));
	}

	public void setEnabled(boolean enabled) {
		interceptingEnabled = enabled;
		logger.info("Intercepting " + (enabled ? "enabled" : "disabled"));
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
		InterceptResult beforeIntResult = processAdvice(false, pipelinePosition, idata, serviceStatus);
		if (beforeIntResult.getException() != null) {
			return; // Exception in before to prevent execution of service/mock
		}
		
		pipelinePosition.setInterceptPoint(INVOKE);
		InterceptResult intResult = processAdvice(true, pipelinePosition, idata, serviceStatus);

		if (intResult.hasIntercepted() && logger.isDebugEnabled()) {
			logger.info("Intercepted: " + ReflectionToStringBuilder.toString(serviceStatus));
		}

		if (!intResult.hasIntercepted() && processorChain.hasNext()) {
			((InvokeChainProcessor) processorChain.next()).process(processorChain, baseService, idata, serviceStatus);
		}

		pipelinePosition.setInterceptPoint(AFTER);
		processAdvice(false, pipelinePosition, idata, serviceStatus);
	}

	private InterceptResult processAdvice(boolean exitOnIntercept, FlowPosition pos, IData idata, ServiceStatus serviceStatus) {
		InterceptResult hasIntercepted = NO_INTERCEPT;
		try {
			for (Advice advice : adviceManager.getAdvicesForInterceptPoint(pos.getInterceptPoint())) {
				if (advice.getAdviceState() == ENABLED && advice.isApplicable(pos, idata)) {
					InterceptResult ir = intercept(pos, idata, serviceStatus, advice);
					if (ir.hasIntercepted()) {
						hasIntercepted = ir; // Ensure its only set, never reset to false
						if (exitOnIntercept) {
							break; // Used to break on first intercept if required
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error intercepting. Behaviour at " + pos + " may be unknown", e);
		}
		if (hasIntercepted.getException() != null) {
			serviceStatus.setException(hasIntercepted.getException());
		}
		return hasIntercepted;
	}

	private InterceptResult intercept(FlowPosition pos, IData idata, ServiceStatus serviceStatus,
			Advice advice) {
		InterceptResult interceptResult = advice.getInterceptor().intercept(pos, idata);
		logger.info("Intercepting " + advice.getId() + " " + pos.getInterceptPoint() + ' ' + pos + " - " + interceptResult.hasIntercepted());
		return interceptResult;
	}

	public void reset(Scope scope) {
		adviceManager.reset(scope);
		if (scope == Scope.ALL) {
			stubManager.clearStubs();
			setEnabled(false);
		}
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
