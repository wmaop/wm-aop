package org.wmaop.aop.advice.remit;

import static org.wmaop.aop.advice.Scope.*;

import org.wmaop.aop.advice.Scope;

import com.wm.app.b2b.server.InvokeState;
import com.wm.app.b2b.server.Session;

public class SessionRemit implements Remit {

	private final String associatedSessionId;
	
	public SessionRemit() {
		associatedSessionId = getSessionID();
	}
	
	public SessionRemit(String associatedSessionId) {
		this.associatedSessionId = associatedSessionId;
	}
	
	@Override
	public boolean isApplicable() {
		return getSessionID().equals(associatedSessionId);
	}

	@Override
	public boolean isApplicable(Scope scope) {
		return (scope == ALL || scope == SESSION) && isApplicable();
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
