package com.nobleprog.solr.p2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import com.nobleprog.solr.BaseIntegrationTest;

/**
 * @author agazzarini
 *
 */
public class SolrBasicFlowExample extends BaseIntegrationTest {
	/**
	 * Finds all indexed documents.
	 */
	@Test
	public void findAll() throws Exception {
		System.err.println(System.getProperty("solr.core.config"));
		
		// This is the list of documents we will send to Solr
		List<SolrInputDocument> books = new ArrayList<SolrInputDocument>();
		
		// Populate the list
		books.add(newBook("1", "Apache Solr Essentials", "Andrea Gazzarini"));
		books.add(newBook("2", "Apache Solr FullText Search Server", "John White"));
		books.add(newBook("3", "Enterprise Search with Apache Solr", "Martin Green"));	
		
		getSolrClient("ex1").add(books);
		getSolrClient("ex1").commit();
		
		// Creates a Solr query
		SolrQuery query = new SolrQuery("*:*");
		
		QueryResponse response = getSolrClient("ex1").query(query);
		
		assertEquals(3, response.getResults().getNumFound());
		
		Set<String> expected = new HashSet<String>(); 
		expected.add("1");
		expected.add("2");
		expected.add("3");
		
		Set<String> result 
			= response.getResults().stream()
				.map(document -> (String) document.getFieldValue("id"))
				.collect(Collectors.toCollection(HashSet::new));
		
		assertEquals(expected, result);
		
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
