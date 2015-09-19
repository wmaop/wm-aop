package com.xlcatlin.wm.mock.bdd;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.xlcatlin.wm.aop.Advice;
import com.xlcatlin.wm.aop.chainprocessor.AOPChainProcessor;
import com.xlcatlin.wm.interceptor.bdd.BddParser;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AOPChainProcessor.class)
public class BddParserTest {

	@Test
	public void shouldParse() throws JAXBException, IOException {
		
		PowerMockito.mockStatic(AOPChainProcessor.class);
		AOPChainProcessor mockProcessor = mock(AOPChainProcessor.class);
		PowerMockito.when(AOPChainProcessor.getInstance()).thenReturn(mockProcessor);
		
		InputStream bddstream = this.getClass().getResourceAsStream("/bdd/validBdd.xml");
		new BddParser().parse(bddstream);

		verify(mockProcessor).registerAdvice(any(Advice.class));
	}

}
