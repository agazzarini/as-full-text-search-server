package com.nobleprog.solr.solr.p2.urp;

import java.io.IOException;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.processor.UpdateRequestProcessor;

/**
 * A simple {@link UpdateRequestProcessor} that removes fields from the incoming document.  
 * 
 * NOTE: we are *in* Solr here, this is not a client!
 */
public class RemoveFieldUpdateRequestProcessor extends UpdateRequestProcessor {

	/**
	 * When is built, a processor wraps the next processor in the chain. 
	 * If the next processor is null, then that means we are in the last processor. 
	 */
	public RemoveFieldUpdateRequestProcessor(UpdateRequestProcessor next) {
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
		
		// Removes the title field.
		// In a real system, the list of fields to be removed should come from the configuration.
		document.removeField("title");
		
		// This is important: if you omit this call the document will be ignored by Solr
		super.processAdd(command);
	}
}
