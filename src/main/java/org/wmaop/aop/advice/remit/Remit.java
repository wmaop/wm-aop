package org.wmaop.aop.advice.remit;

import org.wmaop.aop.advice.Scope;

public interface Remit {
	boolean isApplicable();
	boolean isApplicable(Scope scope);
}
