package org.wmaop.aop.chainprocessor;

import org.wmaop.aop.pipeline.FlowPosition;

import com.wm.data.IData;

public interface Interceptor {

	InterceptResult intercept(FlowPosition flowPosition, IData idata);

}
