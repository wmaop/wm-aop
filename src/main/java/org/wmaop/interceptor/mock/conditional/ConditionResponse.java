package org.wmaop.interceptor.mock.conditional;

public class ConditionResponse {

	private String id;
	private String expression;
	private String response;
	
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
