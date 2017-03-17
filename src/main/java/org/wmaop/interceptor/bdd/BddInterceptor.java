package org.wmaop.interceptor.bdd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.wmaop.aop.interceptor.CompositeInterceptor;
import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptResult;
import org.wmaop.aop.interceptor.InterceptionException;
import org.wmaop.aop.interceptor.Interceptor;
import org.wmaop.aop.matcher.MatchResult;
import org.wmaop.aop.matcher.jexl.JexlIDataMatcher;
import org.wmaop.interceptor.BaseInterceptor;
import org.wmaop.interceptor.bdd.xsd.Scenario;
import org.wmaop.interceptor.bdd.xsd.Then;
import org.wmaop.interceptor.bdd.xsd.When;
import org.wmaop.util.logger.Logger;

import com.wm.data.IData;

public class BddInterceptor extends BaseInterceptor implements CompositeInterceptor {

	public static final String MAP_DEFAULT_INTERCEPTORS = "defaultInterceptors";
	public static final String MAP_INTERCEPTORS = "interceptors";
	public static final String MAP_IGNORE_NO_MATCH = "ignoreNoMatch";

	private static final Logger logger = Logger.getLogger(BddInterceptor.class);

	private final JexlIDataMatcher iDataMatcher;
	/* Locally held and not registered.  Interceptors here are actioned within and not by the chain processor */
	private final Map<String, List<Interceptor>> interceptorMap = new HashMap<>();
	private final List<Interceptor> defaultInterceptors = new ArrayList<>();
	private final boolean ignoreNoMatch;

	private boolean hasExpressions;
	
	public BddInterceptor(Scenario scenario, boolean ignoreNoMatch) {
		super("Scenario:"+scenario.getId());
		Map<String, String> exprs = new LinkedHashMap<>();
		this.ignoreNoMatch = ignoreNoMatch;
		for (When when : scenario.getWhen()) {
			processWhen(exprs, when);
		}
		hasExpressions = !exprs.isEmpty();
		iDataMatcher = new JexlIDataMatcher(exprs);
	}


	@Override
	public List<Interceptor> getInterceptors() {
		List<Interceptor> interceptors = new ArrayList<>();
		interceptors.addAll(defaultInterceptors);
		for(Entry<String, List<Interceptor>> e : interceptorMap.entrySet()) {
			interceptors.addAll(e.getValue());
		}
		return interceptors;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Interceptor> List<T> getInterceptorsOfType(final Class<T> type) {
		
		return (List<T>) interceptorMap.entrySet().stream()
				.flatMap(e -> e.getValue().stream())
				.filter(i -> type.isAssignableFrom(i.getClass()))
				.collect(Collectors.toList());
	}
	
	
	private void processWhen(Map<String, String> exprs, When when) {
		InterceptorFactory intFactory = new InterceptorFactory();
		String id = when.getId();
		String expr = when.getCondition();

		for (Object o : when.getContent()) {
			if (!(o instanceof Then))
				continue;
			Interceptor interceptor = intFactory.getInterceptor((Then) o);
			if (expr != null) {
				exprs.put(id, expr);
				List<Interceptor> am = interceptorMap.get(id);
				if (am == null) {
					am = new ArrayList<>();
					interceptorMap.put(id, am);
				}
				am.add(interceptor);
			} else {
				defaultInterceptors.add(interceptor);
			}
			logger.info("Adding response id " + id + " to action " + interceptor);
		}
	}

	@Override
	public InterceptResult intercept(FlowPosition flowPosition, IData idata) {
		invokeCount++;
		MatchResult result = hasExpressions ? iDataMatcher.match(idata) : null;
		logger.info("Evaluated " + result);

		// Check for match of expression, ignoring if its a non-expression default
		if (result != null && result.isMatch()) {
			return executeActions(interceptorMap.get(result.getId()), flowPosition, idata);
		} else if (!defaultInterceptors.isEmpty()) {
			return executeActions(defaultInterceptors, flowPosition, idata);
		}
		if (ignoreNoMatch) {
			return InterceptResult.TRUE;
		}
		throw new InterceptionException("No conditions match pipeline state");
	}

	private InterceptResult executeActions(List<Interceptor> list, FlowPosition flowPosition, IData idata) {
		Optional<InterceptResult> result = list.stream()
				.map(i -> i.intercept(flowPosition, idata))
				.filter(ir -> ir.getException() != null)
				.findFirst();
		return result.orElse(InterceptResult.TRUE);
	}
	
	@Override
	public void addMap(Map<String, Object> am) {
		am.put(MAP_TYPE, "BddInterceptor");
		am.put(MAP_NAME, name);
		am.put(MAP_INVOKE_COUNT, invokeCount);
		am.put(MAP_IGNORE_NO_MATCH, Boolean.toString(ignoreNoMatch));
		
		Map<String, Object> iterceptors = new HashMap<>();
		for (Entry<String, List<Interceptor>> e : interceptorMap.entrySet()) {
			iterceptors.put(e.getKey(), toMapList(e.getValue()));
		}
		
		am.put(MAP_INTERCEPTORS, iterceptors);
		am.put(MAP_DEFAULT_INTERCEPTORS, toMapList(defaultInterceptors));
	}

	private List<Map<String, Object>> toMapList(List<Interceptor> interceptors) {
		return interceptors.stream()
				.map(Interceptor::toMap)
				.collect(Collectors.toList());
	}
}
