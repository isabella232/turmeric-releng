package org.ebayopensource.turmeric.utils.cassandra.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.UUID;

import me.prettyprint.cassandra.testutils.EmbeddedServerHelper;

import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.config.ConfigurationException;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.config.KSMetaData;
import org.apache.cassandra.db.DefsTable;
import org.apache.cassandra.db.migration.Migration;
import org.apache.cassandra.io.util.FileUtils;
import org.apache.cassandra.service.EmbeddedCassandraService;
import org.apache.thrift.transport.TTransportException;

public class CassandraTestServer {

	private EmbeddedCassandraService cassandra;

	/**
	 * Set embedded cassandra
	 * 
	 * @throws TTransportException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void setup() throws TTransportException, IOException,
			InterruptedException, ConfigurationException {
		loadConfig();

		CassandraServiceDataCleaner cleaner = new CassandraServiceDataCleaner();
		cleaner.prepare();
		cassandra = new EmbeddedCassandraService();
		cassandra.start();
	}

	private static void loadConfig() {
		// TODO use particular test properties, maybe with copy method
		System.setProperty("log4j.configuration",
				"META-INF/config/cassandra/log4j.properties");

		System.setProperty("cassandra.config",
				"META-INF/config/cassandra/cassandra-test.yaml");
	}

	public void teardown() {
		CassandraServiceDataCleaner cleaner = new CassandraServiceDataCleaner();

		try {
			cleaner.cleanupDataDirectories();
			// rmdir("META-INF/config/cassandra/");
		} catch (IOException e) {
			// IGNORE
		}
	}

	private static void rmdir(String dir) throws IOException {
		File dirFile = new File(dir);
		if (dirFile.exists()) {
			FileUtils.deleteRecursive(new File(dir));
		}
	}

	/**
	 * Copies a resource from within the jar to a directory.
	 * 
	 * @param resource
	 * @param directory
	 * @throws IOException
	 */
	private static void copy(String resource, String directory)
			throws IOException {
		mkdir(directory);
		InputStream is = EmbeddedServerHelper.class
				.getResourceAsStream(resource);
		String fileName = resource.substring(resource.lastIndexOf("/") + 1);
		File file = new File(directory + System.getProperty("file.separator")
				+ fileName);
		OutputStream out = new FileOutputStream(file);
		byte buf[] = new byte[1024];
		int len;
		while ((len = is.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		is.close();
	}

	/**
	 * Creates a directory
	 * 
	 * @param dir
	 * @throws IOException
	 */
	private static void mkdir(String dir) throws IOException {
		FileUtils.createDirectory(dir);
	}
}
