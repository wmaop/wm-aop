package com.xlcatlin.wm.aop.matcher.jexl;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;

import com.wm.data.IData;
import com.xlcatlin.util.jexl.IDataJexlContext;
import com.xlcatlin.util.jexl.JexlExpressionFactory;
import com.xlcatlin.wm.aop.matcher.MatchResult;
import com.xlcatlin.wm.aop.matcher.Matcher;

public class JexlIDataMatcher implements Matcher<IData> {

	private final Map<String, Expression> expressions = new LinkedHashMap<String, Expression>();;
	private final String EXPRESSION;
	
	public JexlIDataMatcher(String sid, String expression) {
		createExpression(sid, expression);
		EXPRESSION = expression;
	}

	public JexlIDataMatcher(Map<String, String> exprs) {
		for (Entry<String, String> expr : exprs.entrySet()) {
			createExpression(expr.getKey(), expr.getValue());
		}
		EXPRESSION = Arrays.toString(expressions.values().toArray());
	}

	public MatchResult match(IData idata) {
		JexlContext ctx = new IDataJexlContext(idata);

		for (Entry<String, Expression> expr : expressions.entrySet()) {
			Object result = expr.getValue().evaluate(ctx);
			verifyExpressionResult(expr.getKey(), result);
			if ((Boolean) result)
				return new MatchResult(true, expr.getKey());
		}
		return MatchResult.FALSE;
	}

	private void createExpression(String name, String exprText) {
		Expression compiledExpr = JexlExpressionFactory.createExpression(exprText);
		Object result = compiledExpr.evaluate(new MapContext());
		verifyExpressionResult(name, result);
		expressions.put(name, compiledExpr);
	}
	
	private void verifyExpressionResult(String name, Object result) {
		if (!(result instanceof Boolean)) {
			throw new RuntimeException("Cannot parse expression named '" + name
					+ "' to get boolean, instead got " + result.getClass().getSimpleName() + ": " + result);
		}
	}

	@Override
	public String toString() {
		return "JexlMatcher["+EXPRESSION+']';
	}
	
	
}
