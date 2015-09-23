package com.xlcatlin.wm.interceptor.bdd;

import java.io.IOException;

import com.wm.data.IData;
import com.wm.data.IDataUtil;
import com.wm.util.coder.IDataXMLCoder;
import com.xlcatlin.wm.aop.chainprocessor.InterceptResult;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

public class ReturnAction implements ThenAction {

	private final IData responseIData;

	ReturnAction(String content) throws IOException {
		responseIData = new IDataXMLCoder().decodeFromBytes(content.getBytes());
	}

	@Override
	public InterceptResult execute(FlowPosition flowPosition, IData idata) {
		IDataUtil.merge(responseIData, idata);
		return InterceptResult.TRUE;
	}
}
