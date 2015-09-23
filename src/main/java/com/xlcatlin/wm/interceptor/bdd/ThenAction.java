package com.xlcatlin.wm.interceptor.bdd;

import com.wm.data.IData;
import com.xlcatlin.wm.aop.chainprocessor.InterceptResult;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

public interface ThenAction {

	InterceptResult execute(FlowPosition flowPosition, IData idata);

}
