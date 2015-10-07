package org.wmaop.flow;

import java.io.IOException;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.wmaop.aop.chainprocessor.AOPChainProcessor;
import org.wmaop.aop.chainprocessor.Interceptor;
import org.wmaop.interceptor.assertion.Assertion;
import org.wmaop.interceptor.assertion.AssertionInterceptor;
import org.wmaop.interceptor.assertion.AssertionManager;
import org.wmaop.interceptor.mock.canned.CannedResponseInterceptor;

import com.softwareag.g11n.util.CollectionUtil;
import com.wm.app.b2b.server.ServiceException;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;

public class MockManager extends AbstractFlowManager {

	static final String ADVICE_ID = "adviceId";
	static final String RESPONSE = "response";
	static final String INTERCEPT_POINT = "interceptPoint";
	static final String SERVICE_NAME = "serviceName";
	static final String CONDITION = "condition";

	
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
		} catch (IOException e) {
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
		Assertion assertion = AOPChainProcessor.getInstance().getAssertionManager().getAssertion(adviceId);
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
}
