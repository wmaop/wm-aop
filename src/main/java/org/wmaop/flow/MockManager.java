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

	private static final Logger logger = Logger.getLogger(MockManager.class);
	
	public void registerFixedResponseMock(IData pipeline) throws ServiceException {
		IDataCursor pipelineCursor = pipeline.getCursor();
		String adviceId = IDataUtil.getString(pipelineCursor, "adviceId");
		String interceptPoint = IDataUtil.getString(pipelineCursor, "interceptPoint");
		String serviceName = IDataUtil.getString(pipelineCursor, "serviceName");
		String idata = IDataUtil.getString(pipelineCursor, "response");
		String pipelineCondition = IDataUtil.getString(pipelineCursor, "pipelineCondition");
		pipelineCursor.destroy();

		mandatory(pipeline, "{0} must exist when creating a fixed response mock", "adviceId", "interceptPoint", "serviceName");
		
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
		String adviceId = IDataUtil.getString(pipelineCursor, "adviceId");
		String interceptPoint = IDataUtil.getString(pipelineCursor, "interceptPoint");
		String serviceName = IDataUtil.getString(pipelineCursor, "serviceName");
		String pipelineCondition = IDataUtil.getString(pipelineCursor, "pipelineCondition");
		pipelineCursor.destroy();

		mandatory(pipeline, "{0} must exist when creating an assertion", "adviceId", "interceptPoint", "serviceName");
		registerInterceptor(adviceId, interceptPoint, serviceName, pipelineCondition, new AssertionInterceptor(adviceId));
	}
	
	public void getInvokeCount(IData pipeline) throws ServiceException {
		IDataCursor pipelineCursor = pipeline.getCursor();
		String adviceId = IDataUtil.getString(pipelineCursor, "adviceId");
		pipelineCursor.destroy();

		mandatory(pipeline, "{0} must exist when retrieving assertion count", "adviceId");
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
