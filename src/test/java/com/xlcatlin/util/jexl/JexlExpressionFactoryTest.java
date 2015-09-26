package com.xlcatlin.util.jexl;

import static org.junit.Assert.*;

import org.apache.commons.jexl2.Expression;
import org.junit.Test;

public class JexlExpressionFactoryTest {

	@Test
	public void test() {
		Expression expr = JexlExpressionFactory.createExpression("alpha.beta == \"hello\"");
	}

}
