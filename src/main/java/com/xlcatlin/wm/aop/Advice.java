package com.xlcatlin.wm.aop;

import com.xlcatlin.wm.aop.chainprocessor.Interceptor;

public class Advice {

	private final PointCut pointCut;
	private final Interceptor interceptor;
	private final String id;

	public Advice(String id, PointCut pointCut, Interceptor interceptor) {
		this.pointCut = pointCut;
		this.interceptor = interceptor;
		this.id = id;
	}

	public PointCut getPointCut() {
		return pointCut;
	}

	public Interceptor getInterceptor() {
		return interceptor;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return id + " " + pointCut.toString() + ' ' + interceptor + ' ' + pointCut.getInterceptPoint();
	}

}
