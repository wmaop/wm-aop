package org.wmaop.aop.advice.scope;

public final class GlobalScope implements Scope {

	@Override
	public final boolean isApplicable() {
		return true;
	}

	@Override
	public String toString() {
		return "GlobalScope";
	}
}
