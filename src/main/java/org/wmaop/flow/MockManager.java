package org.wmaop.flow;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wmaop.aop.advice.Advice;
import org.wmaop.aop.advice.AdviceManager;
import org.wmaop.aop.assertion.AssertionInterceptor;
import org.wmaop.aop.interceptor.Interceptor;
import org.wmaop.chainprocessor.AOPChainProcessor;
import org.wmaop.interceptor.mock.canned.CannedResponseInterceptor;
import org.wmaop.interceptor.mock.exception.ExceptionInterceptor;
import org.wmaop.util.pipeline.StructureConverter;

import com.wm.app.b2b.server.ServiceException;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;

public class MockManager extends AbstractFlowManager {

	public static final String ADVICE_ID = "adviceId";
	public static final String RESPONSE = "response";
	public static final String INTERCEPT_POINT = "interceptPoint";
	public static final String SERVICE_NAME = "serviceName";
	public static final String CONDITION = "condition";
	public static final String EXCEPTION = "exception";
	
	private static final Logger logger = Logger.getLogger(MockManager.class);
	
	public void reset() {
		AOPChainProcessor.getInstance().reset();
	}
	
	public void enableInterception(IData pipeline) {
		IDataCursor pipelineCursor = pipeline.getCursor();
		String resourceID = IDataUtil.getString( pipelineCursor, "enabled" );
		
		boolean enabled;
		if (resourceID == null || resourceID.length() == 0) {
			enabled = AOPChainProcessor.getInstance().isEnabled();
		} else {
			enabled = Boolean.valueOf(resourceID);
			AOPChainProcessor.getInstance().setEnabled(enabled);
		}
		
		// pipeline
		IDataUtil.put( pipelineCursor, "enabled", Boolean.toString(enabled) );
		pipelineCursor.destroy();
	}
	
	public void getAdvice(IData pipeline) {
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	adviceId = IDataUtil.getString( pipelineCursor, "id" );
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
		String	id = IDataUtil.getString( pipelineCursor, "id" );
		pipelineCursor.destroy();
		
		AOPChainProcessor.getInstance().getAdviceManager().unregisterAdvice(id);
	}
	
	public void registerFixedResponseMock(IData pipeline) throws ServiceException {
		IDataCursor pipelineCursor = pipeline.getCursor();
		String adviceId = IDataUtil.getString(pipelineCursor, ADVICE_ID);
		String interceptPoint = IDataUtil.getString(pipelineCursor, INTERCEPT_POINT);
		String serviceName = IDataUtil.getString(pipelineCursor, SERVICE_NAME);
		Object idata = IDataUtil.get(pipelineCursor, RESPONSE);
		String pipelineCondition = IDataUtil.getString(pipelineCursor, CONDITION);
		pipelineCursor.destroy();

		mandatory(pipeline, "{0} must exist when creating a fixed response mock", ADVICE_ID, INTERCEPT_POINT, SERVICE_NAME, RESPONSE);
		
		Interceptor interceptor;
		try {
			if (idata instanceof IData) {
				interceptor = new CannedResponseInterceptor((IData)idata);
			} else {
				interceptor = new CannedResponseInterceptor(idata.toString());
			}
		} catch (Exception e) {
			throw new ServiceException("Unable to parse response IData for " + adviceId + " - Is the response valid IData XML? - " + e.getMessage());
		}
		registerInterceptor(adviceId, interceptPoint.toUpperCase(), serviceName, pipelineCondition, interceptor);
	}
	
	public void registerAssertion(IData pipeline) throws ServiceException {
		IDataCursor pipelineCursor = pipeline.getCursor();
		String adviceId = IDataUtil.getString(pipelineCursor, ADVICE_ID);
		String interceptPoint = IDataUtil.getString(pipelineCursor, INTERCEPT_POINT);
		String serviceName = IDataUtil.getString(pipelineCursor, SERVICE_NAME);
		String pipelineCondition = IDataUtil.getString(pipelineCursor, CONDITION);
		pipelineCursor.destroy();

		mandatory(pipeline, "{0} must exist when creating an assertion", ADVICE_ID, INTERCEPT_POINT, SERVICE_NAME);
		registerInterceptor(adviceId, interceptPoint, serviceName, pipelineCondition, new AssertionInterceptor(adviceId));
	}
	
	public void getInvokeCount(IData pipeline) throws ServiceException {
		IDataCursor pipelineCursor = pipeline.getCursor();
		String adviceId = IDataUtil.getString(pipelineCursor, ADVICE_ID);
		pipelineCursor.destroy();

		mandatory(pipeline, "{0} must exist when retrieving assertion count", ADVICE_ID);
		logger.debug("Retrieving assertion " + adviceId);
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
		pipelineCursor.destroy();

		mandatory(pipeline, "{0} must exist when creating an assertion", ADVICE_ID, INTERCEPT_POINT, SERVICE_NAME, EXCEPTION);
		
		try {
			Exception e = (Exception) Class.forName(exception).getDeclaredConstructor(String.class).newInstance("WMAOP " + serviceName);
			registerInterceptor(adviceId, interceptPoint, serviceName, pipelineCondition, new ExceptionInterceptor(e));
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ServiceException(e);
		}
	}
}
