package org.wmaop.aop.advice;

import java.util.HashMap;
import java.util.Map;

import org.wmaop.aop.advice.scope.Scope;
import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.Interceptor;
import org.wmaop.aop.pointcut.PointCut;

import com.wm.data.IData;

public class Advice {

	private final PointCut pointCut;
	private final Interceptor interceptor;
	private final String id;
	private AdviceState adviceState = AdviceState.NEW;
	private final Scope scope;

	public Advice(String id, Scope scope, PointCut pointCut, Interceptor interceptor) {
		this.pointCut = pointCut;
		this.interceptor = interceptor;
		this.id = id;
		this.scope = scope;
	}

	public Scope getScope() {
		return scope;
	}

	public PointCut getPointCut() {
		return pointCut;
	}

	public boolean isApplicable(FlowPosition pipelinePosition, IData idata){
		return scope.isApplicable() && pointCut.isApplicable(pipelinePosition, idata);
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
	
	public Map<String, Object> toMap() {
		Map<String, Object> am = new HashMap<>();
		am.put("state", adviceState.toString());
		am.put("id", id);
		am.put("pointcut", pointCut.toMap());
		am.put("interceptor", interceptor.toMap());
		am.put("scope", scope.toString());
		return am;
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
