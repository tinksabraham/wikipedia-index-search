package lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import parser.PageCallbackHandler;
import parser.WikiPage;
import parser.WikiXMLParser;
import parser.WikiXMLParserFactory;

import org.apache.commons.cli.*;

/**
 * Main application to index wikipedia dump using lucene
 *
 * @author Tinku Abraham
 */
public class WikidumpIndexApplication {

    private static final Logger logger = LogManager.getLogger(WikidumpIndexApplication.class);
    private static String wikiDumpFilePath = null;
    // default
    private static int maxIndexCount = 100;
    private static int counter = 0;

    private static final String WIKI_PATH = "wikiDumpFilePath";
    private static final String MAX_INDEX = "maxIndexCount";
    private static final String HELP = "CLI Help";
    private static final String INDEX_FIELD_ID = "id";
    private static final String INDEX_FIELD_TITLE = "title";
    private static final String INDEX_FIELD_CONTRIBUTOR = "contributor";
    private static final String INDEX_FIELD_TEXT = "text";
    private static final String INDEXED_DIR_NAME = "indexedDir";

    public static void main(String[] args) {

        // command line arguments for wikidump file and max count
        CommandLineParser parser = new DefaultParser();
        Options options = prepareOptions();
        try {
            CommandLine cmd = parser.parse(options, args, true);
            if(cmd.hasOption(WIKI_PATH)) {
                wikiDumpFilePath = cmd.getOptionValue(WIKI_PATH);
            }
            if(cmd.hasOption(MAX_INDEX)) {
                maxIndexCount = Integer.parseInt(cmd.getOptionValue(MAX_INDEX));
            }
        } catch (ParseException ex) {
            logger.error(ex.getMessage());
            new HelpFormatter().printHelp(HELP, options);
            System.exit(1);
        }

        WikidumpIndexApplication main = new WikidumpIndexApplication();
        main.index();
    }

    /**
     *
     * Create the required command line options
     *
     * @return Command line options
     */
    private static Options prepareOptions() {
        Options options = new Options();
        // Required options
        Option pathOption = Option.builder().required()
                .longOpt(WIKI_PATH)
                .desc("path to wikidump file" )
                .hasArg()
                .build();
        Option maxOption = Option.builder().required()
                .longOpt(MAX_INDEX)
                .desc("maximum number of indexed file" )
                .hasArg()
                .build();

        options.addOption(pathOption)
               .addOption(maxOption);

        return options;
    }

    /**
     * Start point of indexing wiki dump file
     */
    private void index() {

        File wikipediaDumpFile = checkWikidumpFileExits();

        if (Files.exists(Paths.get(INDEXED_DIR_NAME))) {
            logger.error("Indexed Directory already exist, please delete it and re-run'");
            System.exit(1);
        }

        // to calculate indexing time as a performance measure
        Date start = new Date();

        try {
            Directory dir = FSDirectory.open(Paths.get(INDEXED_DIR_NAME));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(OpenMode.CREATE);

            // index
            IndexWriter writer = new IndexWriter(dir, iwc);
            indexDocuments(writer, wikipediaDumpFile);
            writer.close();

            // time stamping
            Date end = new Date();
            logger.debug("Indexing time: " + (end.getTime() - start.getTime()) + " total milliseconds for " + WikidumpIndexApplication.counter + " articles.");

        } catch (IOException e) {
            logger.error("Exception: " + e.getMessage());
        }
    }

    /**
     * check if data directory exists
     */
    private File checkWikidumpFileExits() {
        logger.debug("wikipedia dump file = " + wikiDumpFilePath);
        final File wikipediaDumpFile = new File(wikiDumpFilePath);
        if (!wikipediaDumpFile.exists() || !wikipediaDumpFile.canRead()) {
            logger.error("Wikipedia dump file '" + wikipediaDumpFile.getAbsolutePath()
                    + "' does not exist or is not readable, please check the path. ");
            System.exit(1);
        }
        return wikipediaDumpFile;
    }

    /**
     * Indexes individual pages from the wikipedia dump with a set of configured IndexFieldGenerators.
     *
     * @param writer               A writing handle to the index
     * @param file                 The file to be indexed
     * @throws IOException         exception handling for IO
     */
    private void indexDocuments(final IndexWriter writer, File file) throws IOException {

        // reset the file counter
        WikidumpIndexApplication.counter = 0;

        // do not try to index files that cannot be read
        if (file.canRead()) {

            if (file.isDirectory()) {
                String[] files = file.list();

                // an IO error could occur
                if (files != null) {
                    for (String s : files) {
                        indexDocuments(writer, new File(file, s));
                    }
                }

            } else {

                FileInputStream fis;
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException fnfe) {
                    // at least on windows, some temporary files raise an "access denied" message
                    // checking if the file can be read doesn't help
                    return;
                }

                try {

                    final Document doc = new Document();
                    WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser(file.getAbsolutePath());

                    try {
                        wxsp.setPageCallback(new PageCallbackHandler() {
                            public void process(WikiPage page) {

                                if (page.isRedirect())  {
                                    logger.info("Exclude Redirection - " + page.getID() + " about '"  + page.getTitle().trim() + "'");
                                    return;
                                }

                                // check if docId was read successfully, stop if not
                                if (page.getID() == null || page.getID().length() == 0) {
                                    logger.error("Facet Id unknown for wikipedia article '" + page.getTitle() + "'. Nothing done.");
                                    return;
                                }

                                // wikipedia id
                                doc.add(new TextField(INDEX_FIELD_ID, page.getID().trim(), Field.Store.YES));
                                logger.info(INDEX_FIELD_ID + " - " + page.getID().trim());

                                // contributor name -> xml element user name
                                doc.add(new TextField(INDEX_FIELD_CONTRIBUTOR, page.getContributor().trim(), Field.Store.YES));
                                logger.info(INDEX_FIELD_CONTRIBUTOR + " - " + page.getContributor().trim());

                                // article title
                                doc.add(new TextField(INDEX_FIELD_TITLE, page.getTitle().trim(), Field.Store.YES));
                                logger.info(INDEX_FIELD_TITLE + " - " + page.getTitle().trim());

                                // article body text after parsing special characters
                                doc.add(new TextField(INDEX_FIELD_TEXT, page.getText().trim(), Field.Store.YES));

                                // write document to index
                                try {
                                    logger.debug("[" + WikidumpIndexApplication.counter + "] + Adding Wikipedia page id " + page.getID().trim() + " about '" + page.getTitle().trim() + "'");
                                    writer.addDocument(doc);
                                    doc.removeField(INDEX_FIELD_ID);
                                    doc.removeField(INDEX_FIELD_CONTRIBUTOR);
                                    doc.removeField(INDEX_FIELD_TITLE);
                                    doc.removeField(INDEX_FIELD_TEXT);
                                    WikidumpIndexApplication.counter++;

                                    // commit and close the writer once the counter max has reached
                                    // TODO: Check whether to run without max count on server
                                    if (WikidumpIndexApplication.counter == maxIndexCount) {
                                        logger.debug("Last Wiki Article added to index is [ "  + page.getTitle().trim() + "  ]");
                                        writer.commit();
                                        writer.close();
                                        System.exit(0);
                                    }

                                } catch (Exception e) {
                                    logger.error("Exception while writing index: " + e.getMessage());
                                }
                            }

                        });
                        wxsp.parse();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } finally {
                    fis.close();
                }
            }
        }
    }

}
