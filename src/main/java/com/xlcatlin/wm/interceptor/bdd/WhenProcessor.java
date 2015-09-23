package com.xlcatlin.wm.interceptor.bdd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.wm.data.IData;
import com.xlcatlin.wm.aop.chainprocessor.InterceptResult;
import com.xlcatlin.wm.aop.chainprocessor.Interceptor;
import com.xlcatlin.wm.aop.matcher.MatchResult;
import com.xlcatlin.wm.aop.matcher.jexl.JexlIDataMatcher;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;
import com.xlcatlin.wm.interceptor.xsd.bdd.Advice;
import com.xlcatlin.wm.interceptor.xsd.bdd.Then;
import com.xlcatlin.wm.interceptor.xsd.bdd.When;

public class WhenProcessor implements Interceptor {

	private final JexlIDataMatcher evaluator;
	private final Map<String, List<ThenAction>> actionMap = new HashMap<String, List<ThenAction>>();
	private final List<ThenAction> defaultActions = new ArrayList<ThenAction>();
	private final boolean ignoreNoMatch;

	public WhenProcessor(Advice xmlAdvice, boolean ignoreNoMatch) {
		Map<String, String> exprs = new LinkedHashMap<String, String>();
		this.ignoreNoMatch = ignoreNoMatch;
		for (When when : xmlAdvice.getWhen()) {
			String sid = when.getId();
			String expr = when.getCondition();

			for (Object o : when.getContent()) {
				if (!(o instanceof Then))
					continue;
				Then then = (Then) o;
				ThenAction action;
				if (then.getAssert() != null) {
					action = new AssertAction(then.getAssert());
				} else if (then.getReturn() != null) {
					action = new ReturnAction(then.getReturn());
				} else if (then.getPipelineCapture() != null) {
					action = new PipelineCaptureAction(then.getPipelineCapture());
				} else if (then.getThrow() != null) {
					action = new ExceptionAction(then.getThrow());
				} else {
					throw new RuntimeException("No then actions");
				}
				if (expr != null) {
					exprs.put(sid, expr);
					List<ThenAction> am = actionMap.get(sid);
					if (am == null) {
						am = new ArrayList<ThenAction>();
						actionMap.put(sid, am);
					}
					am.add(action);
				} else {
					defaultActions.add(action);
				}
				System.out.println("]>]> Adding response id " + sid + " to action " + action);
			}
		}
		evaluator = new JexlIDataMatcher(exprs);
	}

	public InterceptResult intercept(FlowPosition flowPosition, IData idata) {
		MatchResult result = evaluator.match(idata);
		System.out.println("]>]> Evaluated " + result);
		if (result != null) {
			return executeActions(actionMap.get(result.getId()), flowPosition, idata);
		} else if (defaultActions.size() > 0) {
			return executeActions(defaultActions, flowPosition, idata);
		}
		if (ignoreNoMatch) {
			return InterceptResult.TRUE;
		}
		throw new RuntimeException("No conditions match pipeline state");
	}

	private InterceptResult executeActions(List<ThenAction> list, FlowPosition flowPosition, IData idata) {
		InterceptResult result = InterceptResult.TRUE;
		for (ThenAction action : list) {
			InterceptResult ir = action.execute(flowPosition, idata);
			if (ir.getException() != null) {
				result = ir; // Set Exception as return;
			}
		}
		return result;
	}
}
