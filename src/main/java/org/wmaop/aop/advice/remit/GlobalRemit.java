package org.wmaop.aop.advice.remit;

import org.wmaop.aop.advice.Scope;
import static org.wmaop.aop.advice.Scope.*;

public final class GlobalRemit implements Remit {

	@Override
	public final boolean isApplicable() {
		return true;
	}

	@Override
	public boolean isApplicable(Scope scope) {
		return scope == ALL || scope == GLOBAL;
	}

	@Override
	public String toString() {
		return "GlobalScope";
	}
}
