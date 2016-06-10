package org.wmaop.aop.matcher.jexl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;
import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.matcher.FlowPositionMatcher;
import org.wmaop.aop.matcher.MatchResult;
import org.wmaop.util.jexl.JexlExpressionFactory;

public class JexlFlowPositionMatcher implements FlowPositionMatcher {

	private final Expression expression;
	private final String sid;
	public JexlFlowPositionMatcher(String sid, String expr) {
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

	@Override
	public String getServiceName() {
		return null; // Cant state the exact service because its expression based
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> am = new HashMap<>();
		am.put("type", "JexlFlowPositionMatcher");
		am.put("id", sid);
		am.put("expression", expression.toString());
		return am;
	}
	
}
