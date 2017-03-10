package org.wmaop.aop.advice.remit;

import static org.wmaop.aop.advice.Scope.*;

import org.wmaop.aop.advice.Scope;

import com.wm.app.b2b.server.InvokeState;
import com.wm.app.b2b.server.User;

public class UserRemit implements Remit {

	public static final String DEFAULT_USERNAME = "Default";
	
	private String username;

	public UserRemit() {
		this.username = getCurrentUsername();
	}

	public UserRemit(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	@Override
	public boolean isApplicable() {
		return username.equals(getCurrentUsername());
	}

	@Override
	public boolean isApplicable(Scope scope) {
		return (scope == ALL || scope == USER) && isApplicable();
	}

	@Override
	public String toString() {
		return "UserScope[" + username + ']';
	}
	
	private String getCurrentUsername() {
		User currentUser = InvokeState.getCurrentUser();
		// Verfiy defacto name when not authenticated
		return currentUser == null ? DEFAULT_USERNAME : currentUser.getName();
	}
}
