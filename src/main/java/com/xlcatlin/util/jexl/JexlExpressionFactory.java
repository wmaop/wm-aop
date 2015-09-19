package com.xlcatlin.util.jexl;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlEngine;

public class JexlExpressionFactory {
	 private static final JexlEngine jexlEngine = new JexlEngine();
     static {
        jexlEngine.setCache(512);
        jexlEngine.setLenient(true);
        jexlEngine.setSilent(false);
     }
     
     public static Expression createExpression(String expr)  {
    	 return jexlEngine.createExpression(expr);
     }

	public static JexlEngine getEngine() {
		return jexlEngine;
	}
}
