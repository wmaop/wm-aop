package com.xlcatlin.wm.aop.chainprocessor;

import com.wm.data.IData;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

public interface Interceptor {

	InterceptResult intercept(FlowPosition flowPosition, IData idata);

}
