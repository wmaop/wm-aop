package org.wmaop.interceptor.bdd;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wmaop.chainprocessor.AOPChainProcessor;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AOPChainProcessor.class)
public class BddParserTest {

	@Test
	public void shouldParse() throws Exception {

		PowerMockito.mockStatic(AOPChainProcessor.class);
		AOPChainProcessor mockProcessor = mock(AOPChainProcessor.class);
		PowerMockito.when(AOPChainProcessor.getInstance()).thenReturn(mockProcessor);

		InputStream bddstream = this.getClass().getResourceAsStream("/bdd/multipleReturnBdd.xml");
		ParsedScenario scenario = new BddParser().parse(bddstream, null);
		
		assertEquals("aspect id",scenario.getAdvice().getId());
	}
}
