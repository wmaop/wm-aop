package org.wmaop.aop.matcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.wmaop.aop.interceptor.FlowPosition;

import com.wm.app.b2b.server.InvokeState;
import com.wm.lang.ns.NSService;

public class CallingServicePositionMatcher implements FlowPositionMatcher {

	private final String serviceName;
	private final MatchResult matchTrue;
	private final String serviceNamespacePrefix;

	public CallingServicePositionMatcher(String id, String serviceName, String serviceNamespacePrefix) {
		this.serviceName = serviceName;
		matchTrue = new MatchResult(true, id);
		this.serviceNamespacePrefix = serviceNamespacePrefix;
	}

	public MatchResult match(FlowPosition obj) {
		if (obj == null || !serviceName.equals(obj.toString()))
			return MatchResult.FALSE;
		
		@SuppressWarnings("unchecked")
		Stack<NSService> callStack = InvokeState.getCurrentState().getCallStack();
		if (isCalledWithin(callStack)) {
			return matchTrue;
		}
		return MatchResult.FALSE;
	}

	private boolean isCalledWithin(Stack<NSService> callStack) {
		for (NSService nss : callStack) {
			if (nss.getNSName().getFullName().startsWith(serviceNamespacePrefix)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "CallingServicePositionMatcher["+serviceName+"]";
	}

	@Override
	public String getServiceName() {
		return serviceName;
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> am = new HashMap<>();
		am.put("type", "CallingServicePositionMatcher");
		am.put("serviceName", serviceName);
		return am;
	}
}
