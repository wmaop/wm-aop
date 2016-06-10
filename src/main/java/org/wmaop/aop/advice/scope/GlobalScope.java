package org.wmaop.aop.advice.scope;

public final class GlobalScope implements Scope {

	@Override
	public final boolean isInScope() {
		return true;
	}
}
