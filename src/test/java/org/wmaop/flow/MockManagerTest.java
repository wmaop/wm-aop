package org.wmaop.flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.wmaop.flow.MockManager.ADVICE_ID;
import static org.wmaop.flow.MockManager.CONDITION;
import static org.wmaop.flow.MockManager.ENABLED;
import static org.wmaop.flow.MockManager.EXCEPTION;
import static org.wmaop.flow.MockManager.INTERCEPT_POINT;
import static org.wmaop.flow.MockManager.RESPONSE;
import static org.wmaop.flow.MockManager.SCOPE;
import static org.wmaop.flow.MockManager.SERVICE_NAME;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.wmaop.aop.advice.Advice;
import org.wmaop.aop.advice.AdviceManager;
import org.wmaop.aop.advice.Scope;
import org.wmaop.aop.advice.remit.GlobalRemit;
import org.wmaop.aop.advice.remit.SessionRemit;
import org.wmaop.aop.advice.remit.UserRemit;
import org.wmaop.aop.interceptor.InterceptPoint;
import org.wmaop.aop.matcher.jexl.JexlIDataMatcher;
import org.wmaop.aop.pointcut.ServicePipelinePointCut;
import org.wmaop.chainprocessor.AOPChainProcessor;

import com.wm.app.b2b.server.ServiceException;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import com.wm.util.coder.IDataXMLCoder;

public class MockManagerTest {

	AOPChainProcessor acpMock;
	MockManager mm;

	@Before
	public void setup() {
		acpMock = mock(AOPChainProcessor.class);
		mm = new MockManager();
		AOPChainProcessor.setInstance(acpMock);
	}

	@Test
	public void shouldExerciseReset() throws ServiceException {
		// No scope
		mm.reset(IDataFactory.create());
		verify(acpMock).reset(null);
		reset(acpMock);

		// Invalid scope
		try {
			mm.reset(createIData(new String[][] { { SCOPE, "foo" } }));
			fail();
		} catch (ServiceException e) {
			assertTrue(e.getMessage().contains("Unknown scope"));
		}

		// Correct scope handling
		mm.reset(createIData(new String[][] { { SCOPE, "global" } }));
		verify(acpMock).reset(Scope.GLOBAL);

	}

	@Test
	public void shouldExerciseEnablement() {
		mm.enableInterception(IDataFactory.create());
		verify(acpMock).isEnabled();
		reset(acpMock);

		mm.enableInterception(createIData(new String[][] { { ENABLED, "" } }));
		verify(acpMock).isEnabled();
		reset(acpMock);

		mm.enableInterception(createIData(new String[][] { { ENABLED, "false" } }));
		verify(acpMock).setEnabled(false);
		reset(acpMock);
	}

	@Test
	public void shouldGetAdvice() {
		AdviceManager amMock = mock(AdviceManager.class);
		when(acpMock.getAdviceManager()).thenReturn(amMock);
		mm.getAdvice(IDataFactory.create());
		verify(amMock).listAdvice();
		reset(amMock);

		mm.getAdvice(createIData(new String[][] { { ADVICE_ID, "" } }));
		verify(amMock).listAdvice();
		reset(amMock);

		Advice adviceMock = mock(Advice.class);
		when(adviceMock.getId()).thenReturn("id1");
		when(adviceMock.toMap()).thenReturn(new HashMap<String, Object>());
		when(amMock.getAdvice("foo")).thenReturn(adviceMock);
		mm.getAdvice(createIData(new String[][] { { ADVICE_ID, "foo" } }));
		verify(amMock).getAdvice("foo");

	}

	@Test
	public void shouldUnregister() {
		AdviceManager amMock = mock(AdviceManager.class);
		when(acpMock.getAdviceManager()).thenReturn(amMock);
		mm.removeAdvice(IDataFactory.create());
	}

	@Test
	public void shouldVerifyMissingAssertionParameters() throws Exception {
		testForMissingManadatory(new Callback() {
			public void action(IData idata) throws ServiceException {
				mm.registerAssertion(idata);
			}
		}, new String[][] { { ADVICE_ID, "advid" }, { INTERCEPT_POINT, "before" }, { SERVICE_NAME, "foo:bar" } });
	}

	@Test
	public void shouldVerifyMissingExceptionParameters() throws Exception {
		testForMissingManadatory(new Callback() {
			public void action(IData idata) throws ServiceException {
				mm.registerException(idata);
			}
		}, new String[][] { { ADVICE_ID, "advid" }, { INTERCEPT_POINT, "before" },
			{ SERVICE_NAME, "foo:bar" }, {EXCEPTION, "java.lang.Exception"}});
	}

	@Test
	public void shouldRegisterException() throws ServiceException {
		IData idata = createIData(new String[][] { { ADVICE_ID, "id1" }, { INTERCEPT_POINT, "invoke" },
				{ SERVICE_NAME, "foo:bar" }, { CONDITION, "x == 1" }, { SCOPE, "global"}, {EXCEPTION, "java.lang.Exception"}});
		AdviceManager amMock = mock(AdviceManager.class);
		when(acpMock.getAdviceManager()).thenReturn(amMock);

		final ArgumentCaptor<Advice> captor = ArgumentCaptor.forClass(Advice.class);
		mm.registerException(idata);
		verify(amMock).registerAdvice(captor.capture());
		final Advice argument = captor.getValue();
		assertEquals("id1", argument.getId());
		assertEquals(InterceptPoint.INVOKE, argument.getPointCut().getInterceptPoint());
		assertEquals("foo:bar", argument.getPointCut().getFlowPositionMatcher().getServiceName());
		assertTrue(argument.getRemit() instanceof GlobalRemit);
		assertTrue(((ServicePipelinePointCut) argument.getPointCut()).getPipelineMatcher() instanceof JexlIDataMatcher);

	}

	@Test
	public void shouldRegisterAssertion() throws ServiceException {
		IData idata = createIData(new String[][] { { ADVICE_ID, "id1" }, { INTERCEPT_POINT, "before" },
				{ SERVICE_NAME, "foo:bar" }, { CONDITION, "x == 1" }, { SCOPE, "global" } });
		AdviceManager amMock = mock(AdviceManager.class);
		when(acpMock.getAdviceManager()).thenReturn(amMock);

		final ArgumentCaptor<Advice> captor = ArgumentCaptor.forClass(Advice.class);
		mm.registerAssertion(idata);
		verify(amMock).registerAdvice(captor.capture());
		final Advice argument = captor.getValue();
		assertEquals("id1", argument.getId());
		assertEquals(InterceptPoint.BEFORE, argument.getPointCut().getInterceptPoint());
		assertEquals("foo:bar", argument.getPointCut().getFlowPositionMatcher().getServiceName());
		assertTrue(argument.getRemit() instanceof GlobalRemit);
		assertTrue(((ServicePipelinePointCut) argument.getPointCut()).getPipelineMatcher() instanceof JexlIDataMatcher);
	}

	@Test
	public void shouldVerifyMissingFixedResponseParameters() throws Exception {
		testForMissingManadatory(new Callback() {
			public void action(IData idata) throws ServiceException {
				mm.registerFixedResponseMock(idata);
			}
		}, new String[][] { { ADVICE_ID, "advid" }, { INTERCEPT_POINT, "before" }, { RESPONSE, "invalidResponse" },
				{ SERVICE_NAME, "foo:bar" } });
	}

	@Test
	public void shouldGetInvokeCount() throws Exception {
		testForMissingManadatory(new Callback() {
			public void action(IData idata) throws ServiceException {
				mm.getInvokeCount(idata);
			}
		}, new String[][] { { ADVICE_ID, "advid" } });
		
		AdviceManager amMock = mock(AdviceManager.class);
		when(amMock.getInvokeCountForPrefix("id1")).thenReturn(99);
		when(acpMock.getAdviceManager()).thenReturn(amMock);
		IData pipeline = createIData(new String[][] { { ADVICE_ID, "id1" } });
		mm.getInvokeCount(pipeline);
		assertEquals(99, IDataUtil.get(pipeline.getCursor(), "invokeCount"));
	}

	@Test
	public void shouldCheckInvalidParameters() throws Exception {
		IData idata = createIData(new String[][] { { ADVICE_ID, "id" }, { INTERCEPT_POINT, "before" },
				{ SERVICE_NAME, "foo:bar" }, { RESPONSE, "invalid data" } });
		try {
			mm.registerFixedResponseMock(idata);
			fail();
		} catch (Exception e) {
			assertTrue(e.toString().contains("Unable to parse"));
		}

		IDataCursor cursor = idata.getCursor();
		IDataUtil.put(cursor, RESPONSE, new String(new IDataXMLCoder().encodeToBytes(IDataFactory.create())));
		IDataUtil.put(cursor, INTERCEPT_POINT, "Nothing");
		cursor.destroy();

		try {
			mm.registerFixedResponseMock(idata);
			fail();
		} catch (Exception e) {
			assertTrue(e.toString().contains("interceptPoint NOTHING"));
		}
	}

	@Test
	public void shouldParseRemit() throws Exception {
		IData idata = IDataFactory.create();
		IDataCursor cursor = idata.getCursor();
		assertTrue(mm.getRemit(idata) instanceof UserRemit);
		
		IDataUtil.put(cursor, "scope", "global");
		assertTrue(mm.getRemit(idata) instanceof GlobalRemit);
		
		IDataUtil.put(cursor, "scope", "session");
		assertTrue(mm.getRemit(idata) instanceof SessionRemit);
		
		IDataUtil.put(cursor, "scope", "user");
		assertEquals("Default", ((UserRemit)mm.getRemit(idata)).getUsername());
		
		IDataUtil.put(cursor, "username", "foo");
		IDataUtil.put(cursor, "scope", "USER");
		assertEquals("foo", ((UserRemit)mm.getRemit(idata)).getUsername());
		
		IDataUtil.put(cursor, "scope", "all");
		try {
			mm.getRemit(idata); // Not valid
			fail();
		} catch (ServiceException se) {
			// NOOP
		}
	}

	private void testForMissingManadatory(Callback callback, String[][] params) throws Exception {
		for (int i = 0; i < params.length; i++) {
			IData idata = IDataFactory.create();
			addParams(idata, params, i); // Miss out one at a time;
			try {
				callback.action(idata);
				fail("Missed mandatory " + params[i][0]);
			} catch (ServiceException e) {
				assertTrue(e.toString().contains(params[i][0] + " must exist"));
			}
		}
	}

	private void addParams(IData idata, String[][] params, int skipEntry) {
		IDataCursor idc = idata.getCursor();
		for (int j = 0; j < params.length; j++) {
			if (skipEntry == j)
				continue;
			IDataUtil.put(idc, params[j][0], params[j][1]);
		}
		idc.destroy();
	}

	private IData createIData(String[][] params) {
		IData idata = IDataFactory.create();
		addParams(idata, params, Integer.MAX_VALUE);
		return idata;
	}

	interface Callback {
		void action(IData idata) throws ServiceException;
	}

}
