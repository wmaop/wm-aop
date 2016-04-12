package org.wmaop.aop.advice;

import static org.wmaop.aop.advice.AdviceState.DISPOSED;
import static org.wmaop.aop.advice.AdviceState.ENABLED;
import static org.wmaop.aop.advice.AdviceState.NEW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.apache.log4j.Logger;
import org.wmaop.aop.interceptor.AssertableInterceptor;
import org.wmaop.aop.interceptor.InterceptPoint;
import org.wmaop.interceptor.bdd.BddInterceptor;

public class AdviceManager extends Observable {

	private static final Logger logger = Logger.getLogger(AdviceManager.class);
	private static final String PFX = "]>]> ";

	protected final Map<InterceptPoint, List<Advice>> ADVICES = new HashMap<InterceptPoint, List<Advice>>();
	protected final Map<String, Advice> ID_ADVICE = new HashMap<String, Advice>();

	public void registerAdvice(Advice advice) {
		// Register interceptor as assertable to track invocation count, unless it is already
		if (!(advice.getInterceptor() instanceof AssertableInterceptor || advice.getInterceptor() instanceof BddInterceptor)) {
			advice = new AssertableAdvice(advice);
		}
		
		Advice oldAdvice = getAdvice(advice.getId());
		if (oldAdvice != null) {
			unregisterAdvice(oldAdvice);
		}
		ADVICES.get(advice.getPointCut().getInterceptPoint()).add(advice);
		ID_ADVICE.put(advice.getId(), advice);
		
		// Notify if new
		if (advice.getAdviceState() == NEW) {
			setChanged();
			notifyObservers(advice);
		}
		
		advice.setAdviceState(ENABLED);
		setChanged();
		notifyObservers(advice);
		logger.info(PFX + "Registered advice " + advice);
	}

	public void unregisterAdvice(String adviceId) {
		unregisterAdvice(getAdvice(adviceId));
	}

	public void unregisterAdvice(Advice advice) {
		// Possibly wrapped so base the removal on the id
		List<Advice> advcs = ADVICES.get(advice.getPointCut().getInterceptPoint());
		for (Advice advc : advcs) {
			if (advc.getId().equals(advice.getId())) {
				advcs.remove(advc);
				break;
			}
		}
		ID_ADVICE.remove(advice.getId());
		advice.setAdviceState(DISPOSED);
		setChanged();
		notifyObservers(advice);
		//stubManager.unregisterStub(advice);
	}

	public void clearAdvice() {
		for (List<Advice> advs : ADVICES.values()) {
			List<Advice> advCopy = new ArrayList<>(advs);
			for (Advice adv : advCopy) {
				unregisterAdvice(adv);
			}
		}
		logger.info(PFX + "Cleared all Advice");
		//stubManager.clearStubs();
	}

	public Advice getAdvice(String id) {
		return ID_ADVICE.get(id);
	}

	public List<Advice> listAdvice() {
		List<Advice> list = new ArrayList<Advice>();
		for (List<Advice> adv : ADVICES.values()) {
			list.addAll(adv);
		}
		return list;
	}
	
	public void reset() {
		ADVICES.clear();
		ID_ADVICE.clear();
		for (InterceptPoint ip : InterceptPoint.values()) {
			ADVICES.put(ip, new ArrayList<Advice>());
		}
	}

	public List<Advice> getAdvicesForInterceptPoint(InterceptPoint interceptPoint) {
		return ADVICES.get(interceptPoint);
	}
}
