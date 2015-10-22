package org.wmaop.flow;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.wmaop.aop.Advice;
import org.wmaop.aop.chainprocessor.AOPChainProcessor;
import org.wmaop.interceptor.bdd.BddParser;

import com.wm.app.b2b.server.ServiceException;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;
import com.wm.lang.xml.Document;
import com.wm.lang.xml.WMDocumentException;

public class ScenarioManager {

	public void registerScenario(IData pipeline) throws ServiceException {
		IDataCursor pipelineCursor = pipeline.getCursor();
		Object scenarioAsStream = IDataUtil.get(pipelineCursor, "scenarioAsStream");
		String scenarioAsString = IDataUtil.getString(pipelineCursor, "scenarioAsString");
		Document scenarioAsNode = (Document) IDataUtil.get(pipelineCursor, "scenarioAsDocument");
		pipelineCursor.destroy();

		InputStream scenarioStream;
		if (scenarioAsStream != null) {
			scenarioStream = (InputStream) scenarioAsStream;
		} else if (scenarioAsString != null) {
			scenarioStream = new ByteArrayInputStream(scenarioAsString.getBytes());
		} else if (scenarioAsNode != null) {
			StringBuffer sb = new StringBuffer();
			try {
				scenarioAsNode.appendGeneratedMarkup(sb);
			} catch (WMDocumentException e) {
				throw new ServiceException("Error parsing");
			}
			scenarioStream = new ByteArrayInputStream(sb.toString().getBytes());
		} else {
			throw new ServiceException("Must specify the advice xml as an input");
		}
		try {
			Advice advice = new BddParser().parse(scenarioStream);
			AOPChainProcessor aop = AOPChainProcessor.getInstance();
			aop.registerAdvice(advice);
			aop.setEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Error parsing scenario: " + e.getMessage());
		}
	}
}
