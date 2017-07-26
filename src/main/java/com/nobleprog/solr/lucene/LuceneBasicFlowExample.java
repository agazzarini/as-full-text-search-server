package com.nobleprog.solr.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Basic Lucene example.
 */
public class LuceneBasicFlowExample {
	/**
	 * Main entry point. 
	 * 
	 * @param args the command line arguments.
	 * @throws IOException in case of I/O failure.
	 * @throws ParseException in case of Query parse exception.
	 */
	public static void main(String[] args) throws IOException, ParseException {
		// 1. Creates a directory reference. This is where index datafiles will be created.
		Directory directory = FSDirectory.open(new File("/tmp").toPath());
		
		// 2. Creates an IndexWriter
		try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig())) {
			
			// 3. Add some data
			indexSomeData(writer);
			
			// 4. Search
			search(directory);			
			
			writer.deleteAll();
		} 
	}
	
	/**
	 * Indexes three sample records.
	 * 
	 * @param writer the {@link IndexWriter} instance used for storing data.
	 * @throws IOException in case of I/O failure.
	 */
	public static void indexSomeData(IndexWriter writer) throws IOException {
		writer.addDocument(newBook("1", "Apache Solr Essentials", "Andrea Gazzarini"));
		writer.addDocument(newBook("2", "Apache Solr FullText Search Server", "John White"));
		writer.addDocument(newBook("3", "Enterprise Search with Apache Solr", "Martin Green"));	
		writer.commit();
	}
	
	/**
	 * Search sample. 
	 * 
	 * @param directory the index directory.
	 * @throws IOException in case of I/O failure.
	 * @throws ParseException in case of Query parse exception.
	 */	
	public static void search(Directory directory) throws IOException, ParseException {
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		
		Query query = new QueryParser("title", new StandardAnalyzer()).parse("title:Solr");
		TopDocs matches = searcher.search(query, 10);
		
		System.out.println("Search returned " + matches.totalHits + " matches.");
		Arrays.stream(matches.scoreDocs)
			.map(scoreDoc -> luceneDoc(scoreDoc, searcher))
			.forEach(doc -> {
				System.out.println("-------------------------------------");				
				System.out.println("ID:\t" + doc.get("id"));
				System.out.println("TITLE:\t" + doc.get("title"));
				System.out.println("AUTHOR:\t" + doc.get("author"));
				System.out.println("SCORE:\t" + doc.get("score"));
				
			});
	}
	
	/**
	 * Creates a sample Lucene record.
	 * 
	 * @param id the record identifier.
	 * @param title the book title. 
	 * @param author the book author.
	 * @return a sample Lucene record (a book in this example).
	 */
	public static Document newBook(String id, String title, String author) {
		Document book = new Document();
		book.add(new StringField("id", id, Store.YES));
		book.add(new TextField("title", title, Store.YES));
		book.add(new TextField("author", author, Store.YES));
		return book;
	}
	
	/**
	 * Returns a native Lucene Document. 
	 * 
	 * @param match the Document reference with the Lucene internal ID. 
	 * @param searcher the {@link IndexSearcher}, which we'll use for executing searches.
	 * @return a native Lucene Document.
	 */
	public static Document luceneDoc(ScoreDoc match, IndexSearcher searcher) {
		try {
			return searcher.doc(match.doc);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}	
}
