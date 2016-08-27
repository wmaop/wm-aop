package org.wmaop.flow;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.wmaop.aop.stub.StubManager;
import org.wmaop.chainprocessor.AOPChainProcessor;
import org.wmaop.interceptor.bdd.BddParser;
import org.wmaop.interceptor.bdd.ParsedScenario;
import org.wmaop.util.logger.Logger;

import com.wm.app.b2b.server.ServiceException;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;
import com.wm.lang.xml.Document;
import com.wm.lang.xml.WMDocumentException;

public class ScenarioManager {

	private static final Logger logger = Logger.getLogger(StubManager.class);
	
	public void registerScenario(IData pipeline) throws ServiceException {
		
		IDataCursor pipelineCursor = pipeline.getCursor();
		Object scenarioAsStream = IDataUtil.get(pipelineCursor, "scenarioAsStream");
		String scenarioAsString = IDataUtil.getString(pipelineCursor, "scenarioAsString");
		Document scenarioAsNode = (Document) IDataUtil.get(pipelineCursor, "scenarioAsDocument");
		String adviceId = IDataUtil.getString(pipelineCursor, "adviceId"); 
		pipelineCursor.destroy();

		InputStream scenarioStream;
		if (scenarioAsStream != null) {
			scenarioStream = (InputStream) scenarioAsStream;
		} else if (scenarioAsString != null) {
			scenarioStream = new ByteArrayInputStream(scenarioAsString.getBytes());
		} else if (scenarioAsNode != null) {
			StringBuffer sb = new StringBuffer(); //Required for appendGeneratedMarkup signature
			try {
				scenarioAsNode.appendGeneratedMarkup(sb);
			} catch (WMDocumentException e) {
				logger.error("Error parsing scenario", e);
				throw new ServiceException("Error parsing scenario " + e.getMessage());
			}
			
			scenarioStream = new ByteArrayInputStream(sb.toString().getBytes());
		} else {
			throw new ServiceException("Must specify the advice xml as an input");
		}
		
		try {
			ParsedScenario scenario = new BddParser().parse(scenarioStream, adviceId);

			AOPChainProcessor aop = AOPChainProcessor.getInstance();
			aop.getAdviceManager().registerAdvice(scenario.getAdvice());
			aop.getStubManager().registerStubService(scenario.getServiceNames());
			aop.setEnabled(true);
		} catch (Exception e) {
			logger.error("Error parsing scenario", e);
			throw new ServiceException("Error parsing scenario: " + e.getMessage());
		}
	}
}
