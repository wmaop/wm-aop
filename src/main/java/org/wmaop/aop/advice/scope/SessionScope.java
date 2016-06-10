package org.wmaop.aop.advice.scope;

import com.wm.app.b2b.server.InvokeState;

public class SessionScope implements Scope {

	private final String associatedSessionId;
	
	public SessionScope() {
		associatedSessionId = InvokeState.getCurrentSession().getSessionID();
	}
	
	public SessionScope(String associatedSessionId) {
		this.associatedSessionId = associatedSessionId;
	}
	
	@Override
	public boolean isInScope() {
		return InvokeState.getCurrentSession().getSessionID().equals(associatedSessionId);
	}
}
