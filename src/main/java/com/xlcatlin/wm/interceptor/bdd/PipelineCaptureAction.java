package com.xlcatlin.wm.interceptor.bdd;

import com.wm.data.IData;
import com.xlcatlin.wm.aop.chainprocessor.InterceptResult;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;
import com.xlcatlin.wm.interceptor.pipline.PipelineCaptureInterceptor;

public class PipelineCaptureAction implements ThenAction {

	private PipelineCaptureInterceptor interceptor;
	public PipelineCaptureAction(String fileName) {
		interceptor = new PipelineCaptureInterceptor(fileName);
	}
	@Override
	public InterceptResult execute(FlowPosition flowPosition, IData idata) {
		return interceptor.intercept(flowPosition, idata);
	}

}
