package org.wmaop.flow;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wmaop.aop.advice.Advice;
import org.wmaop.aop.advice.AdviceManager;
import org.wmaop.aop.advice.Scope;
import org.wmaop.aop.advice.remit.GlobalRemit;
import org.wmaop.aop.advice.remit.Remit;
import org.wmaop.aop.advice.remit.SessionRemit;
import org.wmaop.aop.advice.remit.UserRemit;
import org.wmaop.aop.assertion.AssertionInterceptor;
import org.wmaop.aop.interceptor.Interceptor;
import org.wmaop.chainprocessor.AOPChainProcessor;
import org.wmaop.interceptor.mock.canned.CannedResponseInterceptor;
import org.wmaop.interceptor.mock.canned.CannedResponseInterceptor.ResponseSequence;
import org.wmaop.interceptor.mock.exception.ExceptionInterceptor;
import org.wmaop.util.logger.Logger;
import org.wmaop.util.pipeline.StructureConverter;

import com.wm.app.b2b.server.ServiceException;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;

public class MockManager extends AbstractFlowManager {

	// Possible query params
	public static final String ADVICE_ID = "adviceId";
	public static final String ENABLED = "enabled";
	public static final String RESPONSE = "response";
	public static final String INTERCEPT_POINT = "interceptPoint";
	public static final String SERVICE_NAME = "serviceName";
	public static final String CONDITION = "condition";
	public static final String EXCEPTION = "exception";
	public static final String SCOPE = "scope";
	public static final String USERNAME = "username";
	public static final String CALLED_BY = "calledBy";
	
	private static final Logger logger = Logger.getLogger(MockManager.class);
	
	public void reset(IData pipeline) throws ServiceException {
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	scope = IDataUtil.getString( pipelineCursor, SCOPE );
		pipelineCursor.destroy();
		
		try {
			Scope applicableScope = scope == null ? null : Scope.valueOf(scope.toUpperCase());
			AOPChainProcessor.getInstance().reset(applicableScope);
		} catch (IllegalArgumentException e) {
			throw new ServiceException("Unknown scope ["+scope+']');
		}
	}
	
	public void enableInterception(IData pipeline) {
		IDataCursor pipelineCursor = pipeline.getCursor();
		String resourceID = IDataUtil.getString( pipelineCursor, ENABLED );
		
		boolean enabled;
		if (resourceID == null || resourceID.length() == 0) {
			enabled = AOPChainProcessor.getInstance().isEnabled();
		} else {
			enabled = Boolean.valueOf(resourceID);
			AOPChainProcessor.getInstance().setEnabled(enabled);
		}
		
		// pipeline
		IDataUtil.put( pipelineCursor, ENABLED, Boolean.toString(enabled) );
		pipelineCursor.destroy();
	}
	
	public void getAdvice(IData pipeline) {
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	adviceId = IDataUtil.getString( pipelineCursor, ADVICE_ID );
		AdviceManager adviceMgr = AOPChainProcessor.getInstance().getAdviceManager();
		Map<String, ?> adviceMap;
		if (adviceId == null || adviceId.length() == 0) {
			adviceMap = adviceToMap(adviceMgr.listAdvice().toArray(new Advice[0]));
		} else {
			adviceMap = adviceToMap(adviceMgr.getAdvice(adviceId));
		}
		IDataUtil.put(pipelineCursor, "advice", new StructureConverter().toIData(adviceMap));
		pipelineCursor.destroy();
	}
	
	Map<String, Object> adviceToMap(Advice... advices) {
		Map<String, Object> adviceMap = new HashMap<>();
		for (Advice adv : advices) {
			adviceMap.put(adv.getId(), adv.toMap());
		}
		return adviceMap;
	}

	public void removeAdvice(IData pipeline) {
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	id = IDataUtil.getString( pipelineCursor, ADVICE_ID);
		pipelineCursor.destroy();
		
		AOPChainProcessor.getInstance().getAdviceManager().unregisterAdvice(id);
	}
	
	@SuppressWarnings("unchecked")
	public void registerFixedResponseMock(IData pipeline) throws ServiceException {
		IDataCursor pipelineCursor = pipeline.getCursor();
		String adviceId = IDataUtil.getString(pipelineCursor, ADVICE_ID);
		String interceptPoint = IDataUtil.getString(pipelineCursor, INTERCEPT_POINT);
		String serviceName = IDataUtil.getString(pipelineCursor, SERVICE_NAME);
		Object idata = IDataUtil.get(pipelineCursor, RESPONSE);
		String pipelineCondition = IDataUtil.getString(pipelineCursor, CONDITION);
		String calledBy = IDataUtil.getString(pipelineCursor, CALLED_BY);
		pipelineCursor.destroy();

		mandatory(pipeline, "{0} must exist when creating a fixed response mock", ADVICE_ID, INTERCEPT_POINT, SERVICE_NAME, RESPONSE);
		
		Interceptor interceptor;
		try {
			if (idata instanceof IData) {
				interceptor = new CannedResponseInterceptor((IData)idata);
			} else if (idata instanceof List){
				interceptor = new CannedResponseInterceptor(ResponseSequence.SEQUENTIAL, (List<String>)idata);
			} else {
				interceptor = new CannedResponseInterceptor(idata.toString());
			}
		} catch (Exception e) { // Catch ICoder exceptions
			throw new ServiceException("Unable to parse response IData for " + adviceId + " - Is the response valid IData XML? - " + e.getMessage());
		}
		registerInterceptor(adviceId, getRemit(pipeline), interceptPoint.toUpperCase(), serviceName, pipelineCondition, interceptor, calledBy);
	}
	
	Remit getRemit(IData pipeline) throws ServiceException {
		IDataCursor pipelineCursor = pipeline.getCursor();
		String requiredScope = IDataUtil.getString(pipelineCursor, SCOPE);
		if (requiredScope == null) {
			return new UserRemit();
		}
		
		Remit remit;
		switch (Scope.valueOf(requiredScope.toUpperCase())) {
		case GLOBAL:
			remit = new GlobalRemit();
			break;
		case SESSION:
			remit = new SessionRemit();
			break;
		case USER:
			String username = IDataUtil.getString(pipelineCursor, USERNAME);
			remit = (username == null || username.length() == 0) ? new UserRemit() : new UserRemit(username);
			break;
		default:
			throw new ServiceException("Inapplicable scope: " + requiredScope);
		}

		return remit;
	}

	public void registerAssertion(IData pipeline) throws ServiceException {
		IDataCursor pipelineCursor = pipeline.getCursor();
		String adviceId = IDataUtil.getString(pipelineCursor, ADVICE_ID);
		String interceptPoint = IDataUtil.getString(pipelineCursor, INTERCEPT_POINT);
		String serviceName = IDataUtil.getString(pipelineCursor, SERVICE_NAME);
		String pipelineCondition = IDataUtil.getString(pipelineCursor, CONDITION);
		String calledBy = IDataUtil.getString(pipelineCursor, CALLED_BY);
		pipelineCursor.destroy();

		mandatory(pipeline, "{0} must exist when creating an assertion", ADVICE_ID, INTERCEPT_POINT, SERVICE_NAME);
		registerInterceptor(adviceId, getRemit(pipeline), interceptPoint, serviceName, pipelineCondition, new AssertionInterceptor(adviceId), calledBy);
	}
	
	public void getInvokeCount(IData pipeline) throws ServiceException {
		IDataCursor pipelineCursor = pipeline.getCursor();
		String adviceId = IDataUtil.getString(pipelineCursor, ADVICE_ID);
		pipelineCursor.destroy();

		mandatory(pipeline, "{0} must exist when retrieving assertion count", ADVICE_ID);
		int invokeCount = AOPChainProcessor.getInstance().getAdviceManager().getInvokeCountForPrefix(adviceId);
		
		pipelineCursor = pipeline.getCursor();
		IDataUtil.put(pipelineCursor, "invokeCount", invokeCount);
		pipelineCursor.destroy();
	}
	
	public void registerException(IData pipeline) throws ServiceException {
		
		IDataCursor pipelineCursor = pipeline.getCursor();
		String adviceId = IDataUtil.getString(pipelineCursor, ADVICE_ID);
		String interceptPoint = IDataUtil.getString(pipelineCursor, INTERCEPT_POINT);
		String serviceName = IDataUtil.getString(pipelineCursor, SERVICE_NAME);
		String pipelineCondition = IDataUtil.getString(pipelineCursor, CONDITION);
		String exception = IDataUtil.getString(pipelineCursor, EXCEPTION);
		String calledBy = IDataUtil.getString(pipelineCursor, CALLED_BY);
		pipelineCursor.destroy();

		mandatory(pipeline, "{0} must exist when creating an assertion", ADVICE_ID, INTERCEPT_POINT, SERVICE_NAME, EXCEPTION);
		
		try {
			Exception e = (Exception) Class.forName(exception).getDeclaredConstructor(String.class).newInstance("WMAOP " + serviceName);
			registerInterceptor(adviceId, getRemit(pipeline), interceptPoint, serviceName, pipelineCondition, new ExceptionInterceptor(e), calledBy);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ServiceException(e);
		}
	}
}
