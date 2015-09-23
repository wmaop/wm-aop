package com.xlcatlin.wm.interceptor.bdd;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.wm.data.IData;
import com.xlcatlin.wm.aop.InterceptPoint;
import com.xlcatlin.wm.aop.chainprocessor.AOPChainProcessor;
import com.xlcatlin.wm.aop.chainprocessor.Interceptor;
import com.xlcatlin.wm.aop.matcher.AlwaysTrueMatcher;
import com.xlcatlin.wm.aop.matcher.FlowPositionMatcher;
import com.xlcatlin.wm.aop.matcher.Matcher;
import com.xlcatlin.wm.aop.matcher.jexl.JexlWrappingMatcher;
import com.xlcatlin.wm.aop.pointcut.ServicePipelinePointCut;
import com.xlcatlin.wm.interceptor.assertion.Assertion;
import com.xlcatlin.wm.interceptor.mock.canned.CannedResponseInterceptor;
import com.xlcatlin.wm.interceptor.xsd.bdd.Advice;
import com.xlcatlin.wm.interceptor.xsd.bdd.Service;
import com.xlcatlin.wm.interceptor.xsd.bdd.When;

/**
 * Pay attention to the Advice class - one from the parse, one from aop
 */
public class BddParser {
	
	class AssertionHolder {
		String whenCondition;
		String whenid;
		String assertionid;
	}

	public void parse(InputStream bddstream) throws JAXBException, IOException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Advice.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Advice xmlAdvice = (Advice) jaxbUnmarshaller.unmarshal(bddstream);
		processAdvice(xmlAdvice);
	}

	private void processAdvice(Advice xmlAdvice) {
		Interceptor interceptor = new WhenProcessor(xmlAdvice, ignoreNoMatch)
		Service service = xmlAdvice.getGiven().getService();
		FlowPositionMatcher flowPositionMatcher = new FlowPositionMatcher(service.getValue()+'_'+service.getIntercepted(), service.getValue());
		System.out.println("Created flow position matcher: " + flowPositionMatcher);
		
		Matcher<? super IData> pipelineMatcher = getMatcher(xmlAdvice.getGiven().getWhen().getCondition(), xmlAdvice.getGiven().getWhen().getId());
		
		ServicePipelinePointCut joinPoint = new ServicePipelinePointCut(flowPositionMatcher, pipelineMatcher);
		System.out.println("Creating joinPoint: " + joinPoint);
		InterceptPoint interceptPoint = InterceptPoint.valueOf(service.getIntercepted().toUpperCase());
		System.out.println("Creating intercept point: " + interceptPoint);
		com.xlcatlin.wm.aop.Advice advice = new com.xlcatlin.wm.aop.Advice(id, joinPoint, interceptor, interceptPoint);
		
		
		System.out.println("Registering advice: " + advice.getId());
		AOPChainProcessor.getInstance().registerAdvice(advice);
	}

	private Interceptor createCannedResponse(List<When> whens) throws IOException {
		String returnValue = "";
		return new CannedResponseInterceptor(returnValue);
	}

	private void processAssertions(Advice xmlAdvice) {
		InterceptPoint interceptPoint = getInterceptPoint(xmlAdvice);
		// Scan for all assertions and 
		List<AssertionHolder> holder = new ArrayList<AssertionHolder>();
		for (When when : xmlAdvice.getWhen()) {
			for (Object o: when.getContent()) {
				Assert assertion = ((Then)o).getAssert();
				if (assertion != null) {
					AssertionHolder ah = new AssertionHolder();
					ah.whenCondition = when.getCondition();
					ah.assertionid = assertion.getId();
				}
			}
		}
		for (AssertionHolder ah : holder) {
			Service service = xmlAdvice.getGiven().getService();
			When givenWhen = xmlAdvice.getGiven().getWhen();
			String assertionCondition = "", assertionId = "";
			if (ah.whenCondition != null && ah.whenCondition.length() > 0) {
				assertionCondition =  " && " + ah.whenCondition;
				assertionId =  + ':' + ah.whenid;
			}
			FlowPositionMatcher flowPositionMatcher = new FlowPositionMatcher(service.getValue()+'_'+service.getIntercepted(), service.getValue());
			System.out.println("Created flow position matcher: " + flowPositionMatcher);
			
			Matcher<? super IData> pipelineMatcher = getMatcher(givenWhen.getCondition() + assertionCondition, givenWhen.getId() + assertionId);
			ServicePipelinePointCut pointCut = new ServicePipelinePointCut(flowPositionMatcher, pipelineMatcher);
			Assertion interceptor = new Assertion(ah.assertionid);

			com.xlcatlin.wm.aop.Advice advice = new com.xlcatlin.wm.aop.Advice(xmlAdvice.getId(), pointCut, interceptor, interceptPoint);
			System.out.println("Registering assertion: " + advice.getId());
			AOPChainProcessor.getInstance().registerAdvice(advice);
			
		}

	}

	private InterceptPoint getInterceptPoint(Advice xmlAdvice) {
		InterceptPoint interceptPoint = InterceptPoint.valueOf(xmlAdvice.getGiven().getService().getIntercepted().toUpperCase());
		System.out.println("Creating intercept point: " + interceptPoint);
		return interceptPoint;
	}

	private ServicePipelinePointCut getJointPoint(Advice xmlAdvice) {
		Service service = xmlAdvice.getGiven().getService();
		When when = xmlAdvice.getGiven().getWhen();
		
		FlowPositionMatcher flowPositionMatcher = new FlowPositionMatcher(service.getValue()+'_'+service.getIntercepted(), service.getValue());
		System.out.println("Created flow position matcher: " + flowPositionMatcher);
		
		Matcher<? super IData> pipelineMatcher = getMatcher(when.getCondition(), when.getId());
		
		ServicePipelinePointCut joinPoint = new ServicePipelinePointCut(flowPositionMatcher, pipelineMatcher);
		return joinPoint;
	}

	private Matcher<? super IData> getMatcher(String condition, String id) {
		Matcher<? super IData> pipelineMatcher;
		if (condition != null && condition.length() > 0) {
			pipelineMatcher = new JexlWrappingMatcher<IData>(id, condition);
		} else {
			pipelineMatcher = new AlwaysTrueMatcher();
		}
		System.out.println("Created pipeline matcher: " + pipelineMatcher);
		return pipelineMatcher;
	}


	private void process(String id, Service service, Then then) throws IOException {
		When when = then.getWhen();
		String returnValue = then.getReturn();
		Interceptor interceptor;
		if (returnValue != null && returnValue.length() > 0) {
			interceptor = new CannedResponseInterceptor(
					returnValue);
		} else {
			throw new RuntimeException("Expecting a <return> with idata");
		}
		
		FlowPositionMatcher flowPositionMatcher = new FlowPositionMatcher(service.getValue()+'_'+service.getIntercepted(), service.getValue());
		System.out.println("Created flow position matcher: " + flowPositionMatcher);
		
		Matcher<? super IData> pipelineMatcher = getPipelineMatcher(when);
		
		ServicePipelinePointCut joinPoint = new ServicePipelinePointCut(flowPositionMatcher, pipelineMatcher);
		System.out.println("Creating joinPoint: " + joinPoint);
		InterceptPoint interceptPoint = InterceptPoint.valueOf(service.getIntercepted().toUpperCase());
		System.out.println("Creating intercept point: " + interceptPoint);
		Advice advice = new com.xlcatlin.wm.aop.Advice(id, joinPoint, interceptor, interceptPoint);
		
		
		System.out.println("Registering advice: " + advice.getId());
		AOPChainProcessor.getInstance().registerAdvice(advice);
	}
}
