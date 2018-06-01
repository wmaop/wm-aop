package org.wmaop.aop.interceptor;

import org.wmaop.util.pipeline.NameParser;

import com.webmethods.util.Pair;

public class FlowPosition {

	public final String packageName;
	public final String serviceName;
	public final String fqname;
	private InterceptPoint interceptPoint;
	
	public FlowPosition(InterceptPoint point, String fqServiceName) {
		interceptPoint = point;
		Pair<String, String> parsedName = NameParser.parseFQServiceName(fqServiceName);
		this.packageName = parsedName.getFirst();
		this.serviceName = parsedName.getSecond();
		fqname = fqServiceName == null?"":fqServiceName;
	}
	
	@Override
	public String toString() {
		return fqname;
	}

	public void setInterceptPoint(InterceptPoint point) {
		interceptPoint = point;
	}

	public InterceptPoint getInterceptPoint() {
		return interceptPoint;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getFqname() {
		return fqname;
	}
}
