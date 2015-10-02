package org.wmaop.aop.matcher;

public class MatchResult {

	public static final MatchResult FALSE = new MatchResult(false, "undefined");
	public static final MatchResult TRUE = new MatchResult(true, "undefined");
	
	private final boolean isMatch;
	private final String id;

	public MatchResult(boolean result, String id) {
		this.id = id;
		isMatch = result;
	}

	public boolean isMatch() {
		return isMatch;
	}

	public String getId() {
		return id;
	}
	
	public String toString() {
		return "MatchResult:"+isMatch+" for " + id;
	}
}
