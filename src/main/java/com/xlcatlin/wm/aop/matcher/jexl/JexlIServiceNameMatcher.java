package com.xlcatlin.wm.aop.matcher.jexl;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;

import com.xlcatlin.util.jexl.JexlExpressionFactory;
import com.xlcatlin.wm.aop.matcher.MatchResult;
import com.xlcatlin.wm.aop.matcher.Matcher;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

public class JexlIServiceNameMatcher implements Matcher<FlowPosition> {

	private final Expression expression;
	private final String sid;
	public JexlIServiceNameMatcher(String sid, String expr) {
		this.sid = sid;
		expression = createExpression(sid, expr);
	}

	public MatchResult match(FlowPosition flowPosition) {
		final JexlContext ctx = new MapContext();
		ctx.set("serviceName", flowPosition.toString());
		Object result = expression.evaluate(ctx);
		verifyExpressionResult(sid, result);
		if ((Boolean) result) {
			return new MatchResult(true, sid);
		}
		return MatchResult.FALSE;
	}

	private Expression createExpression(String name, String exprText) {
		Expression compiledExpr = JexlExpressionFactory.createExpression(exprText);
		Object result = compiledExpr.evaluate(new MapContext());
		verifyExpressionResult(name, result);
		return compiledExpr;
	}
	
	private void verifyExpressionResult(String name, Object result) {
		if (!(result instanceof Boolean)) {
			throw new RuntimeException("Cannot parse expression named '" + name
					+ "' to get boolean, instead got " + result.getClass().getSimpleName() + ": " + result);
		}
	}
}
