package org.wmaop.interceptor.bdd;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.wmaop.aop.advice.Advice;
import org.wmaop.aop.advice.scope.GlobalScope;
import org.wmaop.aop.interceptor.InterceptPoint;
import org.wmaop.aop.interceptor.Interceptor;
import org.wmaop.aop.matcher.AlwaysTrueMatcher;
import org.wmaop.aop.matcher.FlowPositionMatcherImpl;
import org.wmaop.aop.matcher.Matcher;
import org.wmaop.aop.matcher.jexl.JexlIDataMatcher;
import org.wmaop.aop.pointcut.ServicePipelinePointCut;
import org.wmaop.interceptor.bdd.xsd.Scenario;
import org.wmaop.interceptor.bdd.xsd.Service;
import org.wmaop.interceptor.bdd.xsd.When;

import com.wm.data.IData;

public class BddParser {

	private static final Logger logger = Logger.getLogger(BddParser.class);

	class AssertionHolder {
		String whenCondition;
		String whenid;
		String assertionid;
	}

	public ParsedScenario parse(InputStream bddstream) throws JAXBException, IOException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Scenario.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Scenario scenario = (Scenario) jaxbUnmarshaller.unmarshal(bddstream);
		return new ParsedScenario(processAdvice(scenario), scenario.getGiven().getService().getValue());
	}

	private Advice processAdvice(Scenario scenario) {
		Interceptor interceptor = new BddInterceptor(scenario, true);
		return new Advice(scenario.getId(), new GlobalScope(), getJoinPoint(scenario), interceptor);
	}

	private InterceptPoint getInterceptPoint(Scenario scenario) {
		InterceptPoint interceptPoint = InterceptPoint.valueOf(scenario.getGiven().getService().getIntercepted().toUpperCase());
		logger.info("Creating intercept point: " + interceptPoint);
		return interceptPoint;
	}

	private ServicePipelinePointCut getJoinPoint(Scenario scenario) {
		Service service = scenario.getGiven().getService();
		When when = scenario.getGiven().getWhen();

		FlowPositionMatcherImpl flowPositionMatcher = new FlowPositionMatcherImpl(service.getValue() + '_' + service.getIntercepted(), service.getValue());
		logger.info("Created flow position matcher: " + flowPositionMatcher);

		return new ServicePipelinePointCut(flowPositionMatcher, getMatcher(when), getInterceptPoint(scenario));
	}

	private Matcher<? super IData> getMatcher(When when) {
		String condition = null;
		String id = null;
		if (when != null) {
			condition = when.getCondition();
			id = when.getId();
		}
		Matcher<? super IData> pipelineMatcher;
		if (condition != null && condition.length() > 0) {
			pipelineMatcher = new JexlIDataMatcher(id, condition);
		} else {
			pipelineMatcher = new AlwaysTrueMatcher<>(id);
		}
		logger.info("Created pipeline matcher: " + pipelineMatcher);
		return pipelineMatcher;
	}
}
