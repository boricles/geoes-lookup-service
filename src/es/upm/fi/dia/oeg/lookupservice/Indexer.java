package es.upm.fi.dia.oeg.lookupservice;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class Indexer implements Constants {
	
	
	private boolean alreadyCreated = false;
	private String prefix;
	
	private String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n " + 
								 "SELECT ?label ?s WHERE {?s a []. FILTER(REGEX(STR(?s),\"?PREFIX?\")). OPTIONAL { ?s rdfs:label ?label. }.  } limit 200";	
	private String indexDirectory = "/home/vsaquicela/lucene/oeg/lucene-index";
	
	private String endPoint = "http://geo.linkeddata.es/sparql";
	
	public Indexer() {
		
	}
	
	public Indexer (String prefix) {
		this();
		setPrefix(prefix);
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
		queryString = queryString.replace("?PREFIX?", prefix);
	}
	
	public void setEndpoint(String endPoint) {
		this.endPoint = endPoint;
	}
	
	public void setDirectory(String directory) {
		this.indexDirectory = directory;
	}
	
    private Document createDocument(String resource, String label) {

    	Document document = new Document();
    	document.add(new Field ("resource",resource,Field.Store.YES,Field.Index.TOKENIZED));
    	document.add(new Field ("label",resource,Field.Store.YES,Field.Index.TOKENIZED));
		return document;
    }
	
    

    private void indexDocument(Document document) throws Exception {
    	Analyzer analyzer  = new StandardAnalyzer();
        IndexWriter writer = new IndexWriter(indexDirectory, analyzer, !alreadyCreated);
    	if (!alreadyCreated)
    		alreadyCreated = true;
        writer.addDocument(document);
        writer.optimize();
        writer.close();
    }
    
	
	public void indexResultSet(ResultSet result) throws Exception {
		QuerySolution soln;
		RDFNode x,y;
		String res="", label="";
		while (result.hasNext()) {
			soln = result.nextSolution();
			x = soln.get(querySubject);
			y = soln.get(queryLabel);
			
			
			res = x!=null ? x.toString() : "";
			label = y!= null ? y.asLiteral().getValue().toString() : "";
			
	        Document document = createDocument(res, label);
	        System.out.println("Res:"+res+", label:"+label);
	        indexDocument(document);
        } 
	}
	
	public void generateIndex() throws Exception {
		SPARQLEPClient sc = new SPARQLEPClient();
		ResultSet rs = sc.execQueryEndPoint(endPoint, queryString);
		indexResultSet(rs);
	}
	
	public List<String> search(String term){
		List<String> l= new ArrayList<String>();
		
		try {
			
			FSDirectory		directory = FSDirectory.getDirectory(new File(indexDirectory));	
			
			IndexSearcher	searcher  = new IndexSearcher(directory);
			String			textToSearch = term;
			QueryParser		parser	  = new QueryParser("label", new StandardAnalyzer());
			Query			query	  = parser.parse(textToSearch);
			Hits			hits	  = searcher.search(query);
			Document		doc		  = null;
			
			if (hits.length() == 0){
				System.out.println("No se han encontrado coincidencias.");
			} else {
				
				for (int i = 0; i < hits.length(); i++) {
					doc = hits.doc(i);
					System.out.println(doc.get("resource"));
					l.add(doc.get("resource"));
				}
			}
		} catch (Exception ex){
			System.out.println(ex);
		} 
		return l;
	}
	
	public static void main(String []args) {
		try {
			Indexer index = new Indexer();
			index.setPrefix("http://geo.linkeddata.es/resource/");
			index.setEndpoint("http://geo.linkeddata.es/sparql");
			index.generateIndex();
			
			System.out.println("===Done===");
			index.search("marisma");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}

