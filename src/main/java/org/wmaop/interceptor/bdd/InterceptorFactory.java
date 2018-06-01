package org.wmaop.interceptor.bdd;

import org.wmaop.aop.interceptor.Interceptor;
import org.wmaop.interceptor.bdd.xsd.Then;

public interface InterceptorFactory {

	Interceptor getInterceptor(Then then);

}