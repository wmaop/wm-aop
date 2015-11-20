package org.wmaop.aop.chainprocessor;

import java.io.File;

import org.wmaop.aop.advice.Advice;

import com.wm.app.b2b.server.Package;
import com.wm.app.b2b.server.PackageManager;
import com.wm.app.b2b.server.ServerAPI;
import com.wm.app.b2b.server.ServiceSetupException;
import com.wm.app.b2b.server.ns.Namespace;
import com.wm.lang.ns.NSException;
import com.wm.lang.ns.NSName;
import com.wm.lang.ns.NSServiceType;

public class StubManager {

	private static final String SUPPORTING_PKG = "OrgWmaopStubs";

	protected void deleteStubPackage() {
		if (PackageManager.getPackage(SUPPORTING_PKG) != null) {
			return;
		}
		PackageManager.flushPackage(new Package(SUPPORTING_PKG));
	}

	protected void createStubPackage() {
		if (PackageManager.getPackage(SUPPORTING_PKG) != null) {
			return;
		}

		File pkgdir = new File(PackageManager.getPackageDir(), SUPPORTING_PKG);
		if (pkgdir.exists()) {
			return;
		}
		Package pkg = new Package(SUPPORTING_PKG);
		PackageManager.savePackageConfiguration(pkg);
	}

	protected boolean isUnknownService(String svcName) {
		NSName name = NSName.create(svcName);
		return Namespace.getService(name) != null;
	}

	protected void registerStubService(Advice advice) throws ServiceSetupException {
		NSName name = NSName.create(""); // get service name
		ServerAPI.registerService(SUPPORTING_PKG, name, true, NSServiceType.create("flow", "unknown"), null, null,
				null);
	}

	protected void unregisterStubService(String svcName) throws NSException {
		NSName name = NSName.create(svcName);
		Package pkg = PackageManager.getPackage(SUPPORTING_PKG);
		Namespace.current().deleteNode(name, true, pkg);
	}
}
