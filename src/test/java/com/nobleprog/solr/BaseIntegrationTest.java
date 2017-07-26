package com.nobleprog.solr;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.SolrJettyTestBase;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.embedded.JettyConfig;
import org.apache.solr.client.solrj.embedded.JettySolrRunner;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class BaseIntegrationTest extends SolrJettyTestBase {
	protected static JettySolrRunner SOLR;

	static Map<String, SolrClient> CLIENTS = new HashMap<String, SolrClient>();
	
	@BeforeClass
	public static void init() {
		System.setProperty("solr.data.dir", initCoreDataDir.getAbsolutePath());

		try {
			SOLR = createJetty("src/solr",
					JettyConfig.builder()
						.setPort(8983)
						.setContext("/solr")
						.stopAtShutdown(true)
						.build());
		} catch (final Exception exception) {
			throw new RuntimeException(exception);
		}
	}
	
	@AfterClass
	public static void done() {
		CLIENTS.values().stream().forEach(client -> { try { client.close(); } catch (final Exception ignore) {}});
	}
	
	public SolrClient getSolrClient(final String index) {
		{
			if (CLIENTS.containsKey(index)) {
				return CLIENTS.get(index);
			} else {
				SolrClient client = createNewSolrClient(index);
				CLIENTS.put(index, client);
				return client;
			}
		}
	}

	public SolrClient createNewSolrClient(final String index) {
		if (jetty != null) {
			try {
				// setup the client...
				String url = jetty.getBaseUrl().toString() + "/" + index;
				HttpSolrClient client = getHttpSolrClient(url);
				client.setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT);
				client.setDefaultMaxConnectionsPerHost(100);
				client.setMaxTotalConnections(100);
				return client;
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		} else {
			return new EmbeddedSolrServer(h.getCoreContainer(), index);
		}
	}
}