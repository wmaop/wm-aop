package org.wmaop.aop.advice.scope;

import com.wm.app.b2b.server.InvokeState;

public class UserScope implements Scope {

	private String username;

	public UserScope(String username) {
		this.username = username;
	}
	
	@Override
	public boolean isApplicable() {
		return username.equals(InvokeState.getCurrentUser().getName());
	}
}
