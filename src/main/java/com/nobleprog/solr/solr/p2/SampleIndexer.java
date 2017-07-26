package com.nobleprog.solr.solr.p2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

/**
 * Basic Solr indexing example.
 * The example assumes Solr is running on localhost:8983
 */
public class SampleIndexer {
	/**
	 * Main entry point. 
	 * 
	 * @param args the command line arguments.
	 * @throws IOException in case of I/O failure.
	 */
	public static void main(String[] args) throws IOException, SolrServerException {
		
		// This is the list of documents we will send to Solr
		List<SolrInputDocument> books = new ArrayList<SolrInputDocument>();
		
		// Populate the list
		books.add(newBook("1", "Apache Solr Essentials", "Andrea Gazzarini"));
		books.add(newBook("2", "Apache Solr FullText Search Server", "John White"));
		books.add(newBook("3", "Enterprise Search with Apache Solr", "Martin Green"));	
		
		// Creates the Solr remote proxy
		try (SolrClient client = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/ex1").build()) {
			
			// Indexes data
			client.add(books);
			
			// Commits
			client.commit();
		} 
	}
	
	/**
	 * Creates a sample Solr record.
	 * Note that, while in Lucene deals with one object for both input and output operations
	 * Solr has two differnt objects: {@link SolrInputDocument} in input and {@link SolrDocument} in output. 
	 * 
	 * @param id the record identifier.
	 * @param title the book title. 
	 * @param author the book author.
	 * @return a sample Lucene record (a book in this example).
	 */
	public static SolrInputDocument newBook(String id, String title, String author) {
		SolrInputDocument book = new SolrInputDocument();
		book.setField("id", id);
		book.setField("title", title);
		book.setField("author", author);
		return book;
	}
}
