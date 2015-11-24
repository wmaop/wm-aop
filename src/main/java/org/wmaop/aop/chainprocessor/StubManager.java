package org.wmaop.aop.chainprocessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.wmaop.aop.advice.Advice;
import org.wmaop.aop.matcher.FlowPositionMatcher;
import org.wmaop.aop.matcher.Matcher;
import org.wmaop.aop.pipeline.FlowPosition;

import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.Package;
import com.wm.app.b2b.server.PackageManager;
import com.wm.app.b2b.server.ServerAPI;
import com.wm.app.b2b.server.ServiceException;
import com.wm.app.b2b.server.ServiceSetupException;
import com.wm.app.b2b.server.ns.Namespace;
import com.wm.lang.ns.NSException;
import com.wm.lang.ns.NSName;
import com.wm.lang.ns.NSNode;
import com.wm.lang.ns.NSServiceType;

public class StubManager {

	protected static final String SUPPORTING_PKG = "OrgWmaopStubs";

	public void registerStubService(String... svcNames) {
		createStubPackage(); // Ensure package exists
		for (String svcName : svcNames) {
			if (isRegisteredService(svcName)) {
				continue; // existing service so no stub required
			}
			try {
				NSName name = NSName.create(svcName);
				NSServiceType serviceType = NSServiceType.create("flow", "unknown");
				ServerAPI.registerService(SUPPORTING_PKG, name, true, serviceType , null, null, null);
			} catch (ServiceSetupException e) {
				throw new RuntimeException("Error creating stub service for " + svcName, e);
			}
		}
	}

	public void unregisterStubService(String svcName) throws NSException {
		Package pkg = PackageManager.getPackage(SUPPORTING_PKG);
		if (pkg != null) {
			NSName name = NSName.create(svcName);
			BaseService svc = Namespace.getService(name);
			if (svc != null && SUPPORTING_PKG.equals(svc.getPackageName())) {
				Namespace.current().deleteNode(name, true, pkg);
			}
		}
	}

	public void clearStubs()  {
		Package pkg = PackageManager.getPackage(SUPPORTING_PKG);
		if (pkg == null) {
			return;
		}
		for (Object node : Namespace.current().getNodes(pkg)) {
			try {
				Namespace.current().deleteNode(((NSNode) node).getNSName(), true, pkg);
			} catch (NSException e) {
				throw new RuntimeException(e);
			}
		}
	}

	protected void unregisterStub(Advice advice)  {
		String svcName = getServiceName(advice);
		try {
			unregisterStubService(svcName);
		} catch (NSException e) {
			throw new RuntimeException("Error unregistering stub", e);
		}
	}	

	protected void deleteStubPackage() {
		Package pkg = PackageManager.getPackage(SUPPORTING_PKG);
		if (pkg != null) {
			PackageManager.flushPackage(pkg);
		}
	}

	protected void createStubPackage() {
		if (PackageManager.getPackage(SUPPORTING_PKG) != null) {
			return;
		}
		File pkgdir = new File(PackageManager.getPackageDir(), SUPPORTING_PKG);
		if (pkgdir.exists()) {
			try {
				deleteFolder(pkgdir.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Package pkg = new Package(SUPPORTING_PKG);
		PackageManager.savePackageConfiguration(pkg);
		PackageManager.loadPackage(SUPPORTING_PKG);
	}

	protected boolean isRegisteredService(String svcName) {
		NSName name = NSName.create(svcName);
		return Namespace.getService(name) != null;
	}

	protected String getServiceName(Advice advice) {
		String svcName = null;
		Matcher<FlowPosition> flowPositionMatcher = advice.getPointCut().getFlowPositionMatcher();
		if (flowPositionMatcher instanceof FlowPositionMatcher) {
			svcName = ((FlowPositionMatcher)flowPositionMatcher).getServiceName();
		}
		return svcName;
	}

	protected boolean deleteFolder(String location) throws IOException {
		java.nio.file.Path path = Paths.get(location);
		Files.walkFileTree(path, new SimpleFileVisitor<java.nio.file.Path>() {
			@Override
			public FileVisitResult postVisitDirectory(java.nio.file.Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
			@Override
			public FileVisitResult visitFile(java.nio.file.Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}
		});
		return true;
	}
}
