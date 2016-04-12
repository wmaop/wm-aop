package org.wmaop.aop.interceptor;

import com.wm.data.IData;

public interface Interceptor {

	InterceptResult intercept(FlowPosition flowPosition, IData idata);

}
