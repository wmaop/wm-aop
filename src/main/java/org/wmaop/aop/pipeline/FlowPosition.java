package org.wmaop.aop.pipeline;

import org.wmaop.aop.InterceptPoint;

public class FlowPosition {

	public final String packageName;
	public final String serviceName;
	public final String fqname;
	private InterceptPoint interceptPoint;
	
	public FlowPosition(InterceptPoint point, String fqname) {
		interceptPoint = point;
		this.fqname = fqname;
		if (fqname == null) {
			serviceName = "";
			packageName = "";
			return;
		}
		int pkgsep = fqname.lastIndexOf(':');
		if (pkgsep == -1) {
			serviceName = fqname;
			packageName = "";
		} else {
			serviceName = fqname.substring(pkgsep + 1);
			packageName = fqname.substring(0, pkgsep);
		}
	}
	
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
