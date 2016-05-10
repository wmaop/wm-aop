package org.wmaop.aop.interceptor;

import java.util.Map;

import com.wm.data.IData;

public interface Interceptor {

	InterceptResult intercept(FlowPosition flowPosition, IData idata);

	int getInvokeCount();

	String getName();

	Map<String, Object> toMap();

}
