package org.wmaop.aop.interceptor;

import java.util.List;

public interface CompositeInterceptor extends Interceptor {

	<T extends Interceptor> List<T> getInterceptorsOfType(Class<T> type);
	
	List<Interceptor> getInterceptors();
}
