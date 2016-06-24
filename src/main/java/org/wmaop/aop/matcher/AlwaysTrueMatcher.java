package org.wmaop.aop.matcher;

import java.util.HashMap;
import java.util.Map;

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

	@Override
	public MatchResult match(Object value) {
		return result;
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> am = new HashMap<>();
		am.put("id",  id);
		am.put("type", "AlwaysTrueMatcher");
		return am;
	}

}
