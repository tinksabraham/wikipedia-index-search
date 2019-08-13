package com.wiki.search.services;

import com.wiki.search.models.WikiArticleModel;
import com.wiki.search.exception.IndexIOException;
import com.wiki.search.exception.IndexSearchParseException;
import com.wiki.search.exception.RestError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.apache.lucene.search.Query;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class LuceneSearchService {

    @Value("${indexed.directory.path}")
    private String directory;

    private final Logger log = LogManager.getLogger(this.getClass());

    private static final String INDEX_FIELD_ID = "id";
    private static final String INDEX_FIELD_TITLE = "title";
    private static final String INDEX_FIELD_CONTRIBUTOR = "contributor";

    /**
     *
     * Response generator for indexed search documents
     *
     * @param searchTerm
     * @param searchSize
     * @return response of view models of found articles
     */
    public List<WikiArticleModel> search(String searchTerm, String searchSize) {

        List<WikiArticleModel> wikiArticleModelList = new ArrayList<>();
        IndexSearcher searcher;

        TopDocs foundDocs = null;
        searcher = createSearcher();
        foundDocs = searchByContent(searchTerm, searcher, searchSize);

        // logger.debug("Total Results :: " + foundDocs.totalHits);
        if(searcher!= null && foundDocs != null) {
            wikiArticleModelList = generateWikiArticleModelList(foundDocs, searcher);
        }

        return wikiArticleModelList;
    }

    /**
     *
     * Creates an index searcher on indexed directory
     *
     * @return index searcher
     * @throws IndexIOException
     */
    private IndexSearcher createSearcher() throws IndexIOException{
        IndexSearcher indexSearcher = null;
        IndexReader reader;

        // if paths not found, it will create a directory
        try (Directory dir = FSDirectory.open(Paths.get(directory))) {
            reader = DirectoryReader.open(dir);
            indexSearcher = new IndexSearcher(reader);
        } catch (IOException ex) {
            throwCustomIOException(ex);
        }

        return indexSearcher;
    }

    /**
     *
     * Search the index files for the query
     *
     * @param textContent
     * @param searcher
     * @return docs found for query
     * @throws IndexSearchParseException
     * @throws IndexIOException
     */
    private TopDocs searchByContent(String textContent, IndexSearcher searcher, String searchSize) throws IndexSearchParseException, IndexIOException {
        Query firstNameQuery = null;
        TopDocs topDocs = null;

        // indexed fields to be searched
        String[] indexFieldList = {"contributor", "text"};
        MultiFieldQueryParser mulFieldQueryParser = new MultiFieldQueryParser(indexFieldList, new StandardAnalyzer());

        try {
            firstNameQuery = mulFieldQueryParser.parse(textContent);
        } catch (ParseException ex) {
            throw new IndexSearchParseException(new RestError("Parse- wild card not allowed as first term or wrong search term", ex.getClass().getSimpleName() + ": " + ex.getMessage(),
                    HttpStatus.BAD_REQUEST.value()));
        }

        if (firstNameQuery != null) {
            try {
                topDocs =  searcher.search(firstNameQuery, Integer.parseInt(searchSize));
            } catch (IOException ex) {
                throwCustomIOException(ex);
            }
        }
        return topDocs;
    }

    /**
     *
     * Generate the response model for search call
     *
     * @param foundDocs
     * @param searcher
     * @return view model for api call
     */
    private List<WikiArticleModel> generateWikiArticleModelList(TopDocs foundDocs, IndexSearcher searcher) {

        List<WikiArticleModel> wikiArticleModelList = new ArrayList<>();

        for (ScoreDoc sd : foundDocs.scoreDocs) {
            try {
                Document d = searcher.doc(sd.doc);
                if(d!= null) {
                    WikiArticleModel wikiArticleModel = new WikiArticleModel();
                    wikiArticleModel.setId(d.get(INDEX_FIELD_ID)!= null? d.get(INDEX_FIELD_ID): "");
                    wikiArticleModel.setContributor(d.get(INDEX_FIELD_CONTRIBUTOR)!= null? d.get(INDEX_FIELD_CONTRIBUTOR): "");
                    wikiArticleModel.setArticle(d.get(INDEX_FIELD_TITLE)!= null? d.get(INDEX_FIELD_TITLE): "");
                    wikiArticleModelList.add(wikiArticleModel);
                }
            } catch (IOException ex) {
                throwCustomIOException(ex);
            }
        }
        return wikiArticleModelList;
    }

    private void throwCustomIOException(IOException ex) throws IndexIOException {
        log.info("IO issue");
        throw new IndexIOException(new RestError("Index- directory cannot be searched or found", ex.getClass().getSimpleName() + ": " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

}
