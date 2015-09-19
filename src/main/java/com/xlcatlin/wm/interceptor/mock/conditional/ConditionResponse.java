package com.xlcatlin.wm.interceptor.mock.conditional;

public class ConditionResponse {

	String id;
	String expression;
	String response;
	
	public ConditionResponse(String id, String expression, String response) {
		super();
		this.id = id;
		this.expression = expression;
		this.response = response;
	}

	public String getId() {
		return id;
	}

	public String getExpression() {
		return expression;
	}

	public String getResponse() {
		return response;
	}
	
}
