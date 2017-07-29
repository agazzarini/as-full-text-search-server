package com.nobleprog.solr.solr.p2.urp;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.apache.solr.update.processor.UpdateRequestProcessorFactory;

/**
 * This is the factory of the URP, not the URP itself.
 */
public class RemoveFieldUpdateRequestProcessorFactory extends UpdateRequestProcessorFactory {

	@SuppressWarnings("rawtypes")
	@Override
	public void init(NamedList args) {
		// This is the place where the processor (factory) receives 
		// the configuration you define in sorlconfig.xml
	}
	
	@Override
	public UpdateRequestProcessor getInstance(
			final SolrQueryRequest req, 
			final SolrQueryResponse res,
			UpdateRequestProcessor next) {
		return new RemoveFieldUpdateRequestProcessor(next);
	}
}
