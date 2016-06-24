package org.wmaop.aop.advice;

import java.util.HashMap;
import java.util.Map;

import org.wmaop.aop.advice.remit.Remit;
import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.Interceptor;
import org.wmaop.aop.pointcut.PointCut;

import com.wm.data.IData;

public class Advice {

	private final PointCut pointCut;
	private final Interceptor interceptor;
	private final String id;
	private AdviceState adviceState = AdviceState.NEW;
	private final Remit remit;

	public Advice(String id, Remit remit, PointCut pointCut, Interceptor interceptor) {
		this.pointCut = pointCut;
		this.interceptor = interceptor;
		this.id = id;
		this.remit = remit;
	}

	public Remit getRemit() {
		return remit;
	}

	public PointCut getPointCut() {
		return pointCut;
	}

	public boolean isApplicable(FlowPosition pipelinePosition, IData idata){
		return pointCut.isApplicable(pipelinePosition, idata) && remit.isApplicable();
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
		return id + ' ' + adviceState + ' ' + pointCut + ' ' + interceptor + ' ' + pointCut.getInterceptPoint() + ' ' + remit;
	}
	
	public Map<String, Object> toMap() {
		Map<String, Object> am = new HashMap<>();
		am.put("state", adviceState.toString());
		am.put("adviceId", id);
		am.put("pointcut", pointCut.toMap());
		am.put("interceptor", interceptor.toMap());
		am.put("remit", remit.toString());
		return am;
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
