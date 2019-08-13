package parser;

/**
 * Data structures for a wikipedia page.
 *
 */
public class WikiPage {

    private String title = null;
    private WikiTextParser wikiTextParser = null;
    private String id = null;
    private String contributor = null;

    /**
     * Set the page title. This is not intended for direct use.
     *
     * @param title
     */
    public void setTitle(final String title) {
        this.title = title.trim();
    }

    /**
     * Set the wiki text associated with this page.
     * This setter also introduces side effects. This is not intended for direct use.
     *
     * @param wtext wiki-formatted text
     */
    public void setWikiText(final String wtext, String languageCode) {
        wikiTextParser = new WikiTextParser(wtext, languageCode);
    }

    /**
     * Set the wiki text, defaults to English.
     * @param wtext
     */
    public void setWikiText(final String wtext) {
        setWikiText(wtext, "en");
    }


    /**
     * @return a string containing the page title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Use this method to get the wiki text associated with this page.
     * Useful for custom processing the wiki text.
     *
     * @return a string containing the wiki text.
     */
    public String getWikiText() {
        return wikiTextParser.getText();
    }

    /**
     * @return plain text stripped of all wiki formatting.
     */
    public String getText() {
        return wikiTextParser.getPlainText();
    }

    public String getTextBody() {
        return wikiTextParser.getTextBody();
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }

    public String getContributor() {
        return contributor;
    }

    /**
     * Set the user name for the wikipedia article
     *
     * @param contributor
     */
    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    /**
     * @return true if this is a redirection page
     */
    public boolean isRedirect() {
        return wikiTextParser.isRedirect();
    }
}