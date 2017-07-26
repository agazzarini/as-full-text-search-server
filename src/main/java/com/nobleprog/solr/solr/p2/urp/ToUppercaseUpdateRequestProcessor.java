package com.nobleprog.solr.solr.p2.urp;

import java.io.IOException;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.processor.UpdateRequestProcessor;

/**
 * A simple {@link UpdateRequestProcessor} that replaces all fields with the corresponding uppercase version.  
 * 
 * NOTE: we are within Solr here, this is not a client!
 */
public class ToUppercaseUpdateRequestProcessor extends UpdateRequestProcessor {

	/**
	 * When is built, a processor wraps the next processor in the chain. 
	 * If the next processor is null, then that means we are in the last processor. 
	 */
	public ToUppercaseUpdateRequestProcessor(UpdateRequestProcessor next) {
		super(next);
	}
	
	/**
	 * Where is where we can "interfere" with the indexing process of 
	 * the document wrapped in the given command
	 * 
	 * If we want Solr to skip the document at all, just omit this call:
	 * 
	 *  super.processAdd(command);
	 */
	@Override
	public void processAdd(AddUpdateCommand command) throws IOException {
		// Get the document that is going to be indexed
		SolrInputDocument document = command.getSolrInputDocument();
		
		// Get the title
		String title = (String) document.getFieldValue("title");
		
		// and replace it with the uppercase version
		document.setField("title", title.toUpperCase());
		
		// do the same with other fields...

		// This is important: if you omit this call the document will be ignored by Solr
		super.processAdd(command);
	}
}
