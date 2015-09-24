package com.xlcatlin.wm.aop.matcher;

public class AlwaysTrueMatcher<T> implements Matcher<T> {

	@Override
	public String toString() {
		return "AlwaysTrueMatcher";
	}

	public MatchResult match(Object value) {
		return MatchResult.TRUE;
	}

}
