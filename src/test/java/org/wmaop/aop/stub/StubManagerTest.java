package org.wmaop.aop.stub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wmaop.aop.advice.Advice;
import org.wmaop.aop.interceptor.Interceptor;
import org.wmaop.aop.matcher.FlowPositionMatcher;
import org.wmaop.aop.pointcut.PointCut;

import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.Package;
import com.wm.app.b2b.server.PackageManager;
import com.wm.app.b2b.server.Resources;
import com.wm.app.b2b.server.Server;
import com.wm.app.b2b.server.ServerClassLoader;
import com.wm.app.b2b.server.ns.Namespace;
import com.wm.lang.ns.NSException;
import com.wm.lang.ns.NSName;
import com.wm.lang.ns.NSNode;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PackageManager.class,Namespace.class,ServerClassLoader.class,Server.class})
public class StubManagerTest {

	@Rule
	public TemporaryFolder tempFolder= new TemporaryFolder();

	private StubManager stubManager;
	final String SERVICE_NAME = "foo:bar";

	@Before
	public void setup() {
		stubManager = new StubManager();
		PowerMockito.mockStatic(PackageManager.class);
		PowerMockito.mockStatic(ServerClassLoader.class);
		PowerMockito.mockStatic(Server.class);
	}
	
	@Test
	public void testRegisterStubService() {
		assertFalse(stubManager.isRegisteredService("foo:bar"));
		PowerMockito.when(Server.getResources()).thenReturn(new Resources("",false));
		stubManager.registerStubService("foo:bar");
	}

	@Test
	public void testUnregisterStubService() throws NSException {
		BaseService svc = mock(BaseService.class);
		when(svc.getPackageName()).thenReturn(StubManager.SUPPORTING_PKG);
		
		Namespace ns = mock(Namespace.class);
		PowerMockito.mockStatic(Namespace.class);
		PowerMockito.when(Namespace.getService((NSName) any())).thenReturn(svc);
		PowerMockito.when(Namespace.current()).thenReturn(ns);
		
		PowerMockito.mockStatic(PackageManager.class);
		Package pkg = mock(Package.class);
		PowerMockito.when(PackageManager.getPackage(StubManager.SUPPORTING_PKG)).thenReturn(pkg);
		stubManager.unregisterStubService(SERVICE_NAME);
		
		verify(ns, times(1)).deleteNode((NSName) any(), eq(true), eq(pkg));
	}

	@Test
	public void testClearStubs() throws NSException {
		Package pkg = mock(Package.class);
		PowerMockito.when(PackageManager.getPackage(StubManager.SUPPORTING_PKG)).thenReturn(pkg);
	
		Namespace ns = mock(Namespace.class);
		Vector<NSNode> nodes = new Vector<NSNode>();
		nodes.add(mock(NSNode.class));
		when(ns.getNodes(pkg)).thenReturn(nodes);
		
		PowerMockito.mockStatic(Namespace.class);
		PowerMockito.when(Namespace.current()).thenReturn(ns);
		
		stubManager.clearStubs();
		
		verify(ns, times(1)).deleteNode((NSName) any(), eq(true), eq(pkg));
		
	}

	@Test
	public void testUnregisterStub() {
		Advice advice = createAdviceMock();
		stubManager.unregisterStubService(advice);
	}

	@Test
	public void testDeleteStubPackage() {
		Package pkg = mock(Package.class);
		PowerMockito.when(PackageManager.getPackage(StubManager.SUPPORTING_PKG)).thenReturn(pkg);
		stubManager.deleteStubPackage();
		
		PowerMockito.verifyStatic();
		PackageManager.flushPackage(pkg);
	}

	@Test
	public void shouldSkipWithExistingPackagePresetn() {
		Package pkg = mock(Package.class);
		PowerMockito.when(PackageManager.getPackage(StubManager.SUPPORTING_PKG)).thenReturn(pkg);
		stubManager.createStubPackage();
	}

	@Test
	public void shouldCreatePackage() throws IOException {
		File pkgsFolder = tempFolder.newFolder();
		File stubFolder = new File(pkgsFolder.getAbsolutePath()+'/'+StubManager.SUPPORTING_PKG);
		stubFolder.mkdirs();
		PowerMockito.when(Server.getResources()).thenReturn(new Resources(stubFolder.getAbsolutePath(),false));
		PowerMockito.when(PackageManager.getPackageDir()).thenReturn(pkgsFolder);
		stubManager.createStubPackage();
		// Deleted package as part of checks.  No pkg created due to mocking of PackageManager
		assertFalse(stubFolder.exists());
		
		PowerMockito.verifyStatic();
		PackageManager.loadPackage(StubManager.SUPPORTING_PKG);
	}

	@Test
	public void testIsRegisteredService() {
		assertFalse(stubManager.isRegisteredService(SERVICE_NAME));
	}

	@Test
	public void testGetServiceName() {
		Advice advice = createAdviceMock();
		assertEquals(SERVICE_NAME, stubManager.getServiceName(advice));
	}

	private Advice createAdviceMock() {
		PointCut pointcut = mock(PointCut.class);
		FlowPositionMatcher matcher = mock(FlowPositionMatcher.class);
		when(matcher.getServiceName()).thenReturn(SERVICE_NAME);
		when(pointcut.getFlowPositionMatcher()).thenReturn(matcher);
		Advice advice = new Advice("id", pointcut , mock(Interceptor.class));
		return advice;
	}

	@Test
	public void testDeleteFolder() throws IOException {
		File pkgsFolder = tempFolder.newFolder();
		File stubFolder = new File(pkgsFolder.getAbsolutePath()+"/foo/bar");
		stubFolder.mkdirs();
		File tmpFile = new File(pkgsFolder.getAbsolutePath()+"/foo/blah.txt");
		tmpFile.createNewFile();
		assertTrue(new File(pkgsFolder.getAbsolutePath()+"/foo").exists());
		assertTrue(tmpFile.exists());
		stubManager.deleteFolder(pkgsFolder.getAbsolutePath());
		assertFalse(tmpFile.exists());
		assertFalse(new File(pkgsFolder.getAbsolutePath()+"/foo").exists());
	}

}
