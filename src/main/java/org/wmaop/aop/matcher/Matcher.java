package org.wmaop.aop.matcher;

public interface Matcher<T> {
	MatchResult match(T value);
}
