package org.wmaop.aop.matcher.jexl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.MapContext;
import org.wmaop.aop.matcher.MatchResult;
import org.wmaop.aop.matcher.Matcher;
import org.wmaop.util.jexl.IDataJexlContext;
import org.wmaop.util.jexl.JexlExpressionFactory;

import com.wm.data.IData;

public class JexlIDataMatcher implements Matcher<IData> {

	private final Map<String, JexlExpression> expressions = new LinkedHashMap<>();
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

		for (Entry<String, JexlExpression> expr : expressions.entrySet()) {
			Object result = expr.getValue().evaluate(ctx);
			verifyExpressionResult(expr.getKey(), result);
			if ((Boolean) result)
				return new MatchResult(true, expr.getKey());
		}
		return MatchResult.FALSE;
	}

	private void createExpression(String name, String exprText) {
		JexlExpression compiledExpr = JexlExpressionFactory.createExpression(exprText);
		Object result = compiledExpr.evaluate(new MapContext());
		verifyExpressionResult(name, result);
		expressions.put(name, compiledExpr);
	}
	
	private void verifyExpressionResult(String name, Object result) {
		if (!(result instanceof Boolean)) {
			throw new JexlParseException("Cannot parse expression named '" + name
					+ "' to get boolean, instead got " + result.getClass().getSimpleName() + ": " + result);
		}
	}

	@Override
	public String toString() {
		return "JexlMatcher["+EXPRESSION+']';
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> am = new HashMap<>();
		am.put("type", "JexlIDataMatcher");
		for (Entry<String, JexlExpression> e : expressions.entrySet()) {
			am.put(e.getKey(), e.getValue().toString());
		}
		return am;
	}

	
}
