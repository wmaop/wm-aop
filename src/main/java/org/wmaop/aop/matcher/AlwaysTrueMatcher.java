package org.wmaop.aop.matcher;

public class AlwaysTrueMatcher<T> implements Matcher<T> {

	private final MatchResult result;
	private final String id;

	public AlwaysTrueMatcher(String id) {
		result = new MatchResult(true, id);
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "AlwaysTrueMatcher[" + id + ']';
	}

	public MatchResult match(Object value) {
		return result;
	}

}
