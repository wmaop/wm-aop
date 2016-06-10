package org.wmaop.aop.advice.scope;

import org.wmaop.aop.interceptor.FlowPosition;

import com.wm.app.b2b.server.InvokeState;

public class UserScope implements Scope {

	private String username;

	public UserScope(String username) {
		this.username = username;
	}
	
	@Override
	public boolean isInScope() {
		return username.equals(InvokeState.getCurrentUser().getName());
	}
}
