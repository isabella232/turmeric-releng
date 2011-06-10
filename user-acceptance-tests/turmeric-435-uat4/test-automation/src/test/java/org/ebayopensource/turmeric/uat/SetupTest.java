package org.ebayopensource.turmeric.uat;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.wso2.carbon.registry.app.RemoteRegistry;
import org.wso2.carbon.registry.core.Resource;

public class SetupTest {

	private final static String ASSERTIONS_PROJECT_DIR = "src/test/resources/";

	@BeforeClass
	public static void createRequiredAssetsInWso2() {
//		cleanUpResources();
		createLifecycleInfoInWso2();
		createAssertionAssetsInfoInWso2();
	}

	private static void cleanUpResources() {
		try {
			RemoteRegistry _registry = new RemoteRegistry(
					new URL(
							System.getProperty("org.ebayopensource.turmeric.repository.wso2.url")),
					System.getProperty("org.ebayopensource.turmeric.repository.wso2.username"),
					System.getProperty("org.ebayopensource.turmeric.repository.wso2.password"));

			String serviceAssetKey = "/_system/governance/services/http/www/ebayopensource/org/turmeric/uat1/v1/services/HelloWorld";
			String endpointAssetKey = "/_system/governance/endpoints/http/www/ebayopensource/org/turmeric/ep-HelloWorld";
			_registry.removeAssociation(serviceAssetKey, endpointAssetKey,
			"depends");
			if (_registry.resourceExists(serviceAssetKey)
					&& _registry.resourceExists(endpointAssetKey)) {
				_registry.delete(endpointAssetKey);
				_registry.delete(serviceAssetKey);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Error checking turmeric-wo2 assertable assets");
		}
	}

	private static void createAssertionAssetsInfoInWso2() {
		try {
			RemoteRegistry _registry = new RemoteRegistry(
					new URL(
							System.getProperty("org.ebayopensource.turmeric.repository.wso2.url")),
					System.getProperty("org.ebayopensource.turmeric.repository.wso2.username"),
					System.getProperty("org.ebayopensource.turmeric.repository.wso2.password"));
			String assertionScriptKey = null;
			String assertionName = null;
			Collection<String> assertionFileNames = assertionsFiles();
			for (String assertionFileName : assertionFileNames) {
				System.out.println(assertionFileName);
				assertionName = assertionFileName.substring(0,
						assertionFileName.indexOf("."));
				System.out.println(assertionName);
				assertionScriptKey = "/_system/governance/turmeric435/lib/"
						+ assertionName + "Script";
				Resource asset = _registry.newResource();
				asset.setMediaType("application/octet-stream");
				// need to read the lifecyle content from a file
				InputStream lifecycleIstrm = SetupTest.class.getClassLoader()
						.getResourceAsStream(assertionFileName);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						lifecycleIstrm));
				String str;
				StringBuilder strContent = new StringBuilder();
				while ((str = in.readLine()) != null) {
					strContent.append(str + "\n");
				}
				in.close();
				if (strContent.length() > 0) {
					System.out.println(strContent);
					InputStream contentStream = new ByteArrayInputStream(
							strContent.toString().getBytes());
					asset.setContentStream(contentStream);
				}
				_registry.put(assertionScriptKey, asset);

				String assertionKey = "/_system/governance/turmeric435/lib/"
						+ assertionName;

				Resource assertionAsset = _registry.newResource();
				assertionAsset.setMediaType("application/octet-stream");
				// // need to read the lifecyle content from a file
				// InputStream assertionAssetIs =
				// SetupTest.class.getClassLoader()
				// .getResourceAsStream("assertion.txt");
				// BufferedReader bfReader = new BufferedReader(
				// new InputStreamReader(assertionAssetIs));
				// String assertionAssetContentStr;
				// StringBuilder assertionAssetContentStrBldr = new
				// StringBuilder();
				// while ((assertionAssetContentStr = bfReader.readLine()) !=
				// null) {
				// assertionAssetContentStrBldr
				// .append(assertionAssetContentStr);
				// }
				// bfReader.close();
				// if (assertionAssetContentStrBldr.length() > 0) {
				// System.out.println(assertionAssetContentStrBldr);
				// InputStream assertContentStream = new ByteArrayInputStream(
				// assertionAssetContentStrBldr.toString().getBytes());
				// assertionAsset.setContentStream(assertContentStream);
				// }
				assertionAsset.setProperty("assertion-error-severity", "ERROR");
				assertionAsset.setProperty("assertion-processor", "XQUERY");
				assertionAsset.setProperty(
						"org.ebayopensource.turmeric.artifactVersion", "1.0.0");

				_registry.put(assertionKey, assertionAsset);
				// need to link the assertion with the assertable asset
				_registry.addAssociation(assertionKey, assertionScriptKey,
						"depends");

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Error checking turmeric-wo2 assertable assets");
		}
	}

	private static void createLifecycleInfoInWso2() {
		try {
			RemoteRegistry _registry = new RemoteRegistry(
					new URL(
							System.getProperty("org.ebayopensource.turmeric.repository.wso2.url")),
					System.getProperty("org.ebayopensource.turmeric.repository.wso2.username"),
					System.getProperty("org.ebayopensource.turmeric.repository.wso2.password"));
			String assetName = "TurmericLifeCycle";
			String assetKey = "/_system/config/repository/components/org.wso2.carbon.governance/lifecycles/"
					+ assetName;

			if (!_registry.resourceExists(assetKey)) {
				Resource asset = _registry.newResource();
				asset.setMediaType("application/octet-stream");
				// need to read the lifecyle content from a file
				InputStream lifecycleIstrm = SetupTest.class.getClassLoader()
						.getResourceAsStream("TurmericLifecycle.xml");
				BufferedReader in = new BufferedReader(new InputStreamReader(
						lifecycleIstrm));
				String str;
				StringBuilder strContent = new StringBuilder();
				while ((str = in.readLine()) != null) {
					strContent.append(str);
				}
				in.close();
				if (strContent.length() > 0) {
					InputStream contentStream = new ByteArrayInputStream(
							strContent.toString().getBytes("UTF-8"));
					asset.setContentStream(contentStream);
				}
				_registry.put(assetKey, asset);// i put the lifecycle in the
												// wso2 registry instance

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testRequiredAssetsLoadedInWso2() {
		try {
			RemoteRegistry _registry = new RemoteRegistry(
					new URL(
							System.getProperty("org.ebayopensource.turmeric.repository.wso2.url")),
					System.getProperty("org.ebayopensource.turmeric.repository.wso2.username"),
					System.getProperty("org.ebayopensource.turmeric.repository.wso2.password"));
			String assetName = "TurmericLifeCycle";
			String assetKey = "/_system/config/repository/components/org.wso2.carbon.governance/lifecycles/"
					+ assetName;

			if (!_registry.resourceExists(assetKey)) {
				fail("Turmeric lifecycle not loaded in the wso2 instance");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Error checking turmeric-wo2 lifecycle");
		}
	}

	public static Collection<String> assertionsFiles() {
		File folder = new File(ASSERTIONS_PROJECT_DIR);
		OnlyExt onlyXquery = new OnlyExt(".xquery");
		String[] xqueryFiles = folder.list(onlyXquery);
		Collection<String> ret = new ArrayList<String>();
		for (String s : xqueryFiles) {
			System.out.println(s);
			ret.add(s);
		}
		return ret;

	}

	static class OnlyExt implements FilenameFilter {
		private final String ext;

		public OnlyExt(String _ext) {
			ext = _ext;
		}

		public boolean accept(File dir, String name) {

			return name.endsWith(ext);
		}
	}

	public static void main(String args[]) {
		SetupTest test = new SetupTest();
		test.assertionsFiles();
	}

}
