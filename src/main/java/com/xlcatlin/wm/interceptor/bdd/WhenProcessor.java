package com.xlcatlin.wm.interceptor.bdd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.wm.data.IData;
import com.xlcatlin.wm.aop.chainprocessor.InterceptResult;
import com.xlcatlin.wm.aop.chainprocessor.Interceptor;
import com.xlcatlin.wm.aop.matcher.MatchResult;
import com.xlcatlin.wm.aop.matcher.jexl.JexlIDataMatcher;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;
import com.xlcatlin.wm.interceptor.bdd.xsd.Advice;
import com.xlcatlin.wm.interceptor.bdd.xsd.Then;
import com.xlcatlin.wm.interceptor.bdd.xsd.When;

public class WhenProcessor implements Interceptor {

	private static final Logger logger = Logger.getLogger(WhenProcessor.class);

	private final JexlIDataMatcher evaluator;
	private final Map<String, List<Interceptor>> interceptorMap = new HashMap<String, List<Interceptor>>();
	private final List<Interceptor> defaultInterceptors = new ArrayList<Interceptor>();
	private final boolean ignoreNoMatch;

	private boolean hasExpressions;
	
	public WhenProcessor(Advice xmlAdvice, boolean ignoreNoMatch) {
		Map<String, String> exprs = new LinkedHashMap<String, String>();
		this.ignoreNoMatch = ignoreNoMatch;
		InterceptorFactory intFactory = new InterceptorFactory();
		for (When when : xmlAdvice.getWhen()) {
			String sid = when.getId();
			String expr = when.getCondition();

			for (Object o : when.getContent()) {
				if (!(o instanceof Then))
					continue;
				Interceptor interceptor = intFactory.getInterceptor((Then) o);
				if (expr != null) {
					exprs.put(sid, expr);
					List<Interceptor> am = interceptorMap.get(sid);
					if (am == null) {
						am = new ArrayList<Interceptor>();
						interceptorMap.put(sid, am);
					}
					am.add(interceptor);
				} else {
					defaultInterceptors.add(interceptor);
				}
				logger.info("]>]> Adding response id " + sid + " to action " + interceptor);
			}
		}
		hasExpressions = !exprs.isEmpty();
		evaluator = new JexlIDataMatcher(exprs);
	}

	public InterceptResult intercept(FlowPosition flowPosition, IData idata) {
		MatchResult result = hasExpressions ? evaluator.match(idata) : null;
		logger.info("]>]> Evaluated " + result);

		// Check for match of expression, ignoring if its a non-expression default
		if (result != null && result.isMatch()) {
			return executeActions(interceptorMap.get(result.getId()), flowPosition, idata);
		} else if (defaultInterceptors.size() > 0) {
			return executeActions(defaultInterceptors, flowPosition, idata);
		}
		if (ignoreNoMatch) {
			return InterceptResult.TRUE;
		}
		throw new RuntimeException("No conditions match pipeline state");
	}

	private InterceptResult executeActions(List<Interceptor> list, FlowPosition flowPosition, IData idata) {
		InterceptResult result = InterceptResult.TRUE;
		for (Interceptor action : list) {
			InterceptResult ir = action.intercept(flowPosition, idata);
			if (ir.getException() != null) {
				result = ir; // Set Exception as return;
			}
		}
		return result;
	}
}
