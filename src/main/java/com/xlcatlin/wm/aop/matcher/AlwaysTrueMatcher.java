package com.xlcatlin.wm.aop.matcher;

public class AlwaysTrueMatcher implements Matcher<Object> {

	@Override
	public String toString() {
		return "AlwaysTrueMatcher";
	}

	public MatchResult match(Object value) {
		return MatchResult.TRUE;
	}

}
