package org.wmaop.aop;

import org.wmaop.aop.chainprocessor.Interceptor;

public class Advice {

	private final PointCut pointCut;
	private final Interceptor interceptor;
	private final String id;
	private AdviceState adviceState = AdviceState.NEW;

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

	public AdviceState getAdviceState() {
		return adviceState;
	}

	public void setAdviceState(AdviceState adviceState) {
		this.adviceState = adviceState;
	}

	@Override
	public String toString() {
		return id + ' ' + adviceState + ' ' + pointCut + ' ' + interceptor + ' ' + pointCut.getInterceptPoint();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Advice other = (Advice) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
