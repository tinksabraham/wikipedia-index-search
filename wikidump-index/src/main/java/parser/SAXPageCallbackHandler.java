package parser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SAXPageCallbackHandler extends DefaultHandler {

	private static final String XM_ELEMENT_PAGE = "page";
	private static final String XM_ELEMENT_REVISION = "revision";
	private static final String XM_ELEMENT_TITLE = "title";
	private static final String XM_ELEMENT_ID = "id";
	private static final String XM_ELEMENT_TEXT = "text";
	private static final String XM_ELEMENT_USERNAME = "username";
	private static final String XM_ELEMENT_MEDIAWIKI = "mediawiki";

	private boolean insideRevision = false;
	private PageCallbackHandler pageHandler;
	private WikiPage currentPage;
	private String currentTag;

	private StringBuilder currentWikitext;
	private StringBuilder currentTitle;
	private StringBuilder currentID;
	private StringBuilder currentContributor;
	private String language = null;

	public SAXPageCallbackHandler(PageCallbackHandler pageHandler, String language){
		this.pageHandler = pageHandler;
		this.language = language;
	}

	@Override
	public void startElement(String uri, String name, String qName, Attributes attr){
		currentTag = qName;
		if (qName.equals(XM_ELEMENT_PAGE)){
			currentPage = new WikiPage();
			currentWikitext = new StringBuilder("");
			currentTitle = new StringBuilder("");
			currentID = new StringBuilder("");
			currentContributor = new StringBuilder("");
		}

		if (qName.equals(XM_ELEMENT_REVISION)){
			insideRevision = true;
		}

	}

	@Override
	public void endElement(String uri, String name, String qName){
		if (qName.equals(XM_ELEMENT_REVISION)){
			insideRevision = false;
		} else if (qName.equals(XM_ELEMENT_PAGE)){
			currentPage.setTitle(currentTitle.toString());
			currentPage.setID(currentID.toString());
			currentPage.setWikiText(currentWikitext.toString(), language);
			currentPage.setContributor(currentContributor.toString());
			pageHandler.process(currentPage);
		} else if (qName.equals(XM_ELEMENT_MEDIAWIKI)) {
			// notify semaphore to further execute
			pageHandler.complete();
		}
	}

	@Override
	public void characters(char ch[], int start, int length){
		if (currentTag.equals(XM_ELEMENT_TITLE)){
			currentTitle.append(ch, start, length);
		}
		// Avoids looking at the revision ID, only the first ID should be taken, which is wikipedia id
		else if ((currentTag.equals(XM_ELEMENT_ID)) && !insideRevision){
			currentID.append(ch, start, length);
		}
		else if (currentTag.equals(XM_ELEMENT_TEXT)){
			currentWikitext.append(ch, start, length);
		}
		else if (currentTag.equals(XM_ELEMENT_USERNAME)){
			currentContributor.append(ch, start, length);
		}
	}
}