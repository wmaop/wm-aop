package org.wmaop.flow;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.wmaop.aop.chainprocessor.AOPChainProcessor;
import org.wmaop.aop.chainprocessor.Interceptor;
import org.wmaop.interceptor.assertion.Assertable;
import org.wmaop.interceptor.assertion.AssertionInterceptor;
import org.wmaop.interceptor.mock.canned.CannedResponseInterceptor;
import org.wmaop.interceptor.mock.exception.ExceptionInterceptor;

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
	
	public void registerFixedResponseMock(IData pipeline) throws ServiceException {
		IDataCursor pipelineCursor = pipeline.getCursor();
		String adviceId = IDataUtil.getString(pipelineCursor, ADVICE_ID);
		String interceptPoint = IDataUtil.getString(pipelineCursor, INTERCEPT_POINT);
		String serviceName = IDataUtil.getString(pipelineCursor, SERVICE_NAME);
		String idata = IDataUtil.getString(pipelineCursor, RESPONSE);
		String pipelineCondition = IDataUtil.getString(pipelineCursor, CONDITION);
		pipelineCursor.destroy();

		mandatory(pipeline, "{0} must exist when creating a fixed response mock", ADVICE_ID, INTERCEPT_POINT, SERVICE_NAME, RESPONSE);
		
		Interceptor interceptor;
		try {
			interceptor = new CannedResponseInterceptor(idata);
		} catch (Exception e) {
			throw new ServiceException("Unable to parse response IData for " + adviceId);
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
		Assertable assertion = AOPChainProcessor.getInstance().getAssertionManager().getAssertion(adviceId);
		int invokeCount = 0;
		if (assertion == null) {
			logger.warn("]>]> ** No assertion found for " + adviceId);
		} else {
			invokeCount = assertion.getInvokeCount();
		}
		
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
