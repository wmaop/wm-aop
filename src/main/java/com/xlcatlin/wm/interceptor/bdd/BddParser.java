package com.xlcatlin.wm.interceptor.bdd;

import java.io.IOException;
import java.io.InputStream;
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
import com.xlcatlin.wm.interceptor.mock.canned.CannedResponseInterceptor;
import com.xlcatlin.wm.interceptor.xsd.bdd.Advice;
import com.xlcatlin.wm.interceptor.xsd.bdd.Service;
import com.xlcatlin.wm.interceptor.xsd.bdd.When;

/**
 * Pay attention to the Advice class - one from the parse, one from aop
 */
public class BddParser {

	public void parse(InputStream bddstream) throws JAXBException, IOException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Advice.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Advice xmlAdvice = (Advice) jaxbUnmarshaller.unmarshal(bddstream);
		processAdvice(xmlAdvice);
	}

	private void processAdvice(com.xlcatlin.wm.interceptor.xsd.bdd.Advice xmlAdvice) {

		Interceptor interceptor = getInterceptor(xmlAdvice);
		ServicePipelinePointCut joinPoint = getJointPoint(xmlAdvice);
		InterceptPoint interceptPoint = getInterceptPoint(xmlAdvice);

		com.xlcatlin.wm.aop.Advice advice = new com.xlcatlin.wm.aop.Advice(xmlAdvice.getId(), joinPoint, interceptor, interceptPoint);

		System.out.println("Registering advice: " + advice.getId());
		AOPChainProcessor.getInstance().registerAdvice(advice);
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
		
		Matcher<? super IData> pipelineMatcher;
		if (when != null && when.getCondition() != null) {
			pipelineMatcher = new JexlWrappingMatcher<IData>(when.getId(), when.getCondition());
		} else {
			pipelineMatcher = new AlwaysTrueMatcher();
		}
		System.out.println("Created pipeline matcher: " + pipelineMatcher);
		
		ServicePipelinePointCut joinPoint = new ServicePipelinePointCut(flowPositionMatcher, pipelineMatcher);
		return joinPoint;
	}

	private Interceptor getInterceptor(Advice xmlAdvice) {
		List<When> whens = xmlAdvice.getWhen();
		if (whens.size() == 1) {
			return createCannedResponse(whens);
		} else{
			return createConditionalResponse(whens);
		}
	}

	private Interceptor createConditionalResponse(List<When> whens) {
		// TODO Auto-generated method stub
		return null;
	}

	private Interceptor createCannedResponse(List<When> whens) {
		String returnValue = whens.get(0).getContent().get(0)
		return new CannedResponseInterceptor(
				returnValue);
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
		
		Matcher<? super IData> pipelineMatcher;
		if (when != null) {
			pipelineMatcher = new JexlWrappingMatcher<IData>(when.getId(), when.getContent());
		} else {
			pipelineMatcher = new AlwaysTrueMatcher();
		}
		System.out.println("Created pipeline matcher: " + pipelineMatcher);
		
		ServicePipelinePointCut joinPoint = new ServicePipelinePointCut(flowPositionMatcher, pipelineMatcher);
		System.out.println("Creating joinPoint: " + joinPoint);
		InterceptPoint interceptPoint = InterceptPoint.valueOf(service.getIntercepted().toUpperCase());
		System.out.println("Creating intercept point: " + interceptPoint);
		Advice advice = new com.xlcatlin.wm.aop.Advice(id, joinPoint, interceptor, interceptPoint);
		
		
		System.out.println("Registering advice: " + advice.getId());
		AOPChainProcessor.getInstance().registerAdvice(advice);
	}
}
