package org.wmaop.flow;

import java.io.IOException;

import org.wmaop.aop.chainprocessor.Interceptor;
import org.wmaop.interceptor.assertion.Assertion;
import org.wmaop.interceptor.assertion.AssertionInterceptor;
import org.wmaop.interceptor.assertion.AssertionManager;
import org.wmaop.interceptor.mock.canned.CannedResponseInterceptor;

import com.wm.app.b2b.server.ServiceException;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;

public class MockManager extends FlowManager {

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
		Assertion assertion = AssertionManager.getInstance().getAssertion(adviceId);
		
		pipelineCursor = pipeline.getCursor();
		IDataUtil.put(pipelineCursor, "invokeCount", assertion.getInvokeCount());
		pipelineCursor.destroy();
	}
}
