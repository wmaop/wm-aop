package org.wmaop.aop.chainprocessor;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.wm.app.b2b.server.Package;
import com.wm.app.b2b.server.PackageManager;
import com.wm.app.b2b.server.ns.Namespace;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PackageManager.class,Namespace.class})
public class StubManagerTest {

	@Rule
	public TemporaryFolder tempFolder= new TemporaryFolder();

	private PackageManager packageManager;
	private StubManager stubManager;

	@Before
	public void setup() {
		stubManager = new StubManager();
	}
	
	@Test
	public void testRegisterStubService() {
		fail("Not yet implemented");
	}

	@Test
	public void testUnregisterStubService() {
		fail("Not yet implemented");
	}

	@Test
	public void testClearStubs() {
		fail("Not yet implemented");
	}

	@Test
	public void testUnregisterStub() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteStubPackage() {
		fail("Not yet implemented");
	}

	@Test
	public void shouldSkipWithExistingPackagePresetn() {
		PowerMockito.mockStatic(PackageManager.class);
		Package pkg = mock(Package.class);
		PowerMockito.when(PackageManager.getPackage(StubManager.SUPPORTING_PKG)).thenReturn(pkg);
		stubManager.createStubPackage();
	}

	@Test
	public void shouldCreatePackage() throws IOException {
		PowerMockito.mockStatic(PackageManager.class);
		File pkgsFoder = tempFolder.newFolder();
		File stubFolder = new File(pkgsFoder.getAbsolutePath()+'/'+StubManager.SUPPORTING_PKG);
		PowerMockito.when(PackageManager.getPackageDir()).thenReturn(stubFolder );
		stubManager.createStubPackage();
	}

	@Test
	public void testIsRegisteredService() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetServiceName() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteFolder() {
		fail("Not yet implemented");
	}

}
