package com.xlcatlin.wm.aop.matcher.jexl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.ObjectContext;

import com.wm.data.IData;
import com.xlcatlin.util.jexl.IDataJexlContext;
import com.xlcatlin.util.jexl.JexlExpressionFactory;
import com.xlcatlin.wm.aop.matcher.MatchResult;
import com.xlcatlin.wm.aop.matcher.Matcher;

public class JexlWrappingMatcher implements Matcher<IData> {

	private final Map<String, Expression> expressions = new LinkedHashMap<String, Expression>();
	private final String id;
	private final String exprs;
	
	
	public JexlWrappingMatcher(String id, String expression) {
		expressions.put(id, JexlExpressionFactory.createExpression(expression));
		this.id = id;
		this.exprs = expression;
	}

	public JexlWrappingMatcher(Map<String, String> exprs) {
		StringBuilder sb = new StringBuilder(); 
		for (Entry<String, String> expr : exprs.entrySet()) {
			expressions.put(expr.getKey(), JexlExpressionFactory.createExpression(expr.getValue()));
			sb.append('{').append(expr.getKey()).append(':').append(expr.getValue()).append('}');
		}
		this.id = "";
		this.exprs = sb.toString();
	}

	public MatchResult match(IData idata) {
		JexlContext ctx = new ObjectContext<IDataJexlContext>(JexlExpressionFactory.getEngine(), new IDataJexlContext(idata));
		for (Entry<String, Expression> expr : expressions.entrySet()) {
			if ((Boolean) expr.getValue().evaluate(ctx))
				return new MatchResult(true, expr.getKey());
		}
		return MatchResult.FALSE;
	}

	public String toString() {
		return "JexlWrappingMatcher["+id+','+exprs+']';
	}
}
