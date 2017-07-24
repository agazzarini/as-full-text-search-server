package com.nobleprog.solr.p1;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Basic Lucene example.
 * 
 * @author agazzarini
 */
public class LuceneBasicFlowExampleTestCase {
	
	private IndexWriter writer;
	private Directory directory;
	
	/**
	 * Setup all what we need for this test case. 
	 * 
	 * @throws IOException hopefully never, otherwise the test will fail.
	 */
	@Before
	public void setUp() throws IOException {
		// 1. Creates a directory reference. This is where index datafiles will be created.
		directory = FSDirectory.open(new File("/tmp").toPath());
		
		// 2. Creates an IndexWriter
		writer = new IndexWriter(directory, new IndexWriterConfig());

		// 3. Add some data
		writer.addDocument(newBook("1", "Apache Solr Essentials", "Andrea Gazzarini"));
		writer.addDocument(newBook("2", "Apache Solr FullText Search Server", "John White"));
		writer.addDocument(newBook("3", "Enterprise Search with Apache Solr", "Martin Green"));	
		writer.commit();
	}

	/**
	 * Executes a query for all documents in the index. 
	 * 
	 * @throws Exception never, otherwise the test fails.
	 */
	@Test
	public void findAll() throws Exception {
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		
		Query query = new QueryParser("title", new StandardAnalyzer()).parse("title:Solr");
		TopDocs matches = searcher.search(query, 10);
		
		assertEquals(3, matches.totalHits);
		
		Set<String> expected = new HashSet<String>(); 
		expected.add("1");
		expected.add("2");
		expected.add("3");
		
		Set<String> result = Arrays.stream(matches.scoreDocs)
			.map(scoreDoc -> luceneDoc(scoreDoc.doc, searcher))
			.map(doc -> doc.get("id"))
			.collect(Collectors.toCollection(HashSet::new));
		
		assertEquals(expected, result);
	}
	
	/**
	 * Cleans up the index and releases resources. 
	 * 
	 * @throws IOException hopefully never, otherwise the test will fail.
	 */
	@After
	public void tearDown() throws IOException {
		writer.deleteAll();
		writer.close();
	}
	
	private Document newBook(String id, String title, String author) {
		Document book = new Document();
		book.add(new StringField("id", id, Store.YES));
		book.add(new TextField("title", title, Store.YES));
		book.add(new TextField("author", author, Store.YES));
		return book;
	}
	
	private Document luceneDoc(int id, IndexSearcher searcher) {
		try {
			return searcher.doc(id);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}	
}
