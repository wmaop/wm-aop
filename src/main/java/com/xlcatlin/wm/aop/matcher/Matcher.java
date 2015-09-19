package com.xlcatlin.wm.aop.matcher;

public interface Matcher<T> {
	MatchResult match(T value);
}
