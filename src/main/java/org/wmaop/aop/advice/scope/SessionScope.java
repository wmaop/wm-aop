package org.wmaop.aop.advice.scope;

import com.wm.app.b2b.server.InvokeState;
import com.wm.app.b2b.server.Session;

public class SessionScope implements Scope {

	private final String associatedSessionId;
	
	public SessionScope() {
		associatedSessionId = getSessionID();
	}
	
	public SessionScope(String associatedSessionId) {
		this.associatedSessionId = associatedSessionId;
	}
	
	@Override
	public boolean isApplicable() {
		return getSessionID().equals(associatedSessionId);
	}


	@Override
	public String toString() {
		return "SessionScope[" + getSessionID() + ']';
	}
	
	private String getSessionID() {
		Session session = InvokeState.getCurrentSession();
		final String id;
		if (session == null) {
			id = "NoSession";
		} else {
			id = session.getSessionID();
		}
		return id;
	}
}
