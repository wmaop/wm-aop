package com.xlcatlin.wm.interceptor.bdd;

import com.wm.data.IData;
import com.xlcatlin.wm.aop.chainprocessor.InterceptResult;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;
import com.xlcatlin.wm.interceptor.bdd.xsd.Assert;

public class AssertAction implements ThenAction {

	public AssertAction(Assert assert1) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public InterceptResult execute(FlowPosition flowPosition, IData idata) {
		// TODO Auto-generated method stub
		return InterceptResult.TRUE;
	}

}
