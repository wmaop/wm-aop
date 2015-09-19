package com.xlcatlin.wm.aop.matcher.jexl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.ObjectContext;

import com.xlcatlin.util.jexl.JexlExpressionFactory;
import com.xlcatlin.wm.aop.matcher.MatchResult;
import com.xlcatlin.wm.aop.matcher.Matcher;

public class JexlWrappingMatcher<T> implements Matcher<T> {

	private final Map<String, Expression> expressions = new LinkedHashMap<String, Expression>();

	public JexlWrappingMatcher(String id, String expression) {
		expressions.put(id, JexlExpressionFactory.createExpression(expression));
	}

	public JexlWrappingMatcher(Map<String, String> exprs) {
		for (Entry<String, String> expr : exprs.entrySet()) {
			expressions.put(expr.getKey(), JexlExpressionFactory.createExpression(expr.getValue()));
		}
	}

	public MatchResult match(T wrapped) {
		JexlContext ctx = new ObjectContext<T>(JexlExpressionFactory.getEngine(), wrapped);
		for (Entry<String, Expression> expr : expressions.entrySet()) {
			if ((Boolean) expr.getValue().evaluate(ctx))
				return new MatchResult(true, expr.getKey());
		}
		return MatchResult.FALSE;
	}

}
