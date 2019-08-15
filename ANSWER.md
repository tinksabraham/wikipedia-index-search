# Explanation

## a) How did you approach extracting tokens from the article bodies?
I have used Apache Lucene for indexing wikidump file. Lucene provides analyzer class to analyze 
a document and get tokens/words from the article bodies. I used the **StandardAnalyzer** for the 
index writer config. I have extracted xml title tag and removed all special characters with text parser and then added 
as [*TEXT FIELD*](https://lucene.apache.org/core/7_0_1/core/org/apache/lucene/document/TextField.html) 
lucene index document.

## b) What would you need to change in your tools to return ranked results?
Lucene comes out of box with scoring. It basically returns the search results based on term frequency (tf) and 
inverse document frequency (idf). We can also improve the ranked results by implementing custom search boosting depending
on our criteria either at index time or query time. With my test results, standard boost algorithm returns the search query very fast.

## c) How would your solution change if it needed to be run within 1GB of memory? If it runs off of disk, describe what/how many disk operations are needed to perform a query.
Usage of FSDirectory instead of RAMDirectory when there is available memory limits. 
TODO- Investigate on RAMDirectory vs FSDirectory - Check docs on disk IO ops

## e) What parts of the indexing/lookup did you implement yourself?

### Indexing (First tool)
1. Understand process of indexing with Lucene (read document, java api doc- to get better understanding)
2. Use a event driven parser for xml parsing- I used [WikiXMLJ-Apache 2.0 License](https://github.com/delip/wikixmlj) and customize it to my needs.
3. Implemented an Wiki Index application (Maven project) and provide the jar file
4. Check memory usage and implemented a max commit index count in order to reduce memory usage and commit indexed file to Lucene document during indexing process.

### Lookup/search (Second GUI tool)
1. Implemented a Spring boot BE application for searching the indexed file and handle proper exception handling which I feel is important and to be notified in FE.
2. Implemented an Angular FE application to be able to search indexed wikipedia file, it connects to BE application. Handles necessary HTTP interceptors for BE exception.
3. Wrote some unit test for searching the index - *Note* Only for development purpose
4. Use [MultiFieldQueryParser](https://lucene.apache.org/core/7_1_0/queryparser/org/apache/lucene/queryparser/classic/MultiFieldQueryParser.html) for querying and handle parser exception with wild card entry in front. 

## f) If you received a diff of changes to articles (additions and removals) what would be needed to merge them into your solution? - Could you do this while still querying results, if so, how?
Possible solution could be to open index directory in **CREATE_OR_APPEND** mode and find the indexed doc with wikipedia id which is also indexed initially.
[Update document](https://github.com/apache/lucene-solr/blob/master/lucene/core/src/java/org/apache/lucene/index/IndexWriter.java#L98)

Querying can still be done, since IndexReader is not affected by writing, but search result for new additions will happen 
only after [reopening IndexReader](https://github.com/apache/lucene-solr/blob/master/lucene/core/src/java/org/apache/lucene/index/IndexReader.java#L36).  