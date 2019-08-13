package parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * For internal use only -- Used by the {@link WikiPage} class.
 * Can also be used as a stand alone class to parse wiki formatted text.
 *
 */
public class WikiTextParser {
    private String wikiText = null;
    private boolean redirect = false;
    private String redirectString = null;
    private static Pattern redirectPattern = null;
    private static Pattern stylesPattern = Pattern.compile("\\{\\|.*?\\|\\}$", Pattern.MULTILINE | Pattern.DOTALL);
    private static Pattern infoboxCleanupPattern = Pattern.compile("\\{\\{infobox.*?\\}\\}$", Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static Pattern curlyCleanupPattern0 = Pattern.compile("^\\{\\{.*?\\}\\}$", Pattern.MULTILINE | Pattern.DOTALL);
    private static Pattern curlyCleanupPattern1 = Pattern.compile("\\{\\{.*?\\}\\}", Pattern.MULTILINE | Pattern.DOTALL);
    private static Pattern cleanupPattern0 = Pattern.compile("^\\[\\[.*?:.*?\\]\\]$", Pattern.MULTILINE | Pattern.DOTALL);
    private static Pattern cleanupPattern1 = Pattern.compile("\\[\\[(.*?)\\]\\]", Pattern.MULTILINE | Pattern.DOTALL);
    private static Pattern refCleanupPattern = Pattern.compile("<ref>.*?</ref>", Pattern.MULTILINE | Pattern.DOTALL);
    private static Pattern commentsCleanupPattern = Pattern.compile("<!--.*?-->", Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * Default constructor
     * @param wikiText  The wiki text
     * @param languageCode  the language of the currently parsed wikipedia
     */
    public WikiTextParser(String wikiText, String languageCode) {
        this.wikiText = wikiText;
        createPatterns();
        findRedirect(wikiText);
    }

    /**
     * Check for redirects
     * @param wikiText  the currently parsed page
     */
    private void findRedirect(String wikiText) {
        Matcher matcher = redirectPattern.matcher(wikiText);
        if (matcher.find()) {
            redirect = true;
            if (matcher.groupCount() == 1) {
                redirectString = matcher.group(1);
            }
        }
    }
    /**
     * Create localized patterns (given the language in the constructor) for redirects, stubs, etc.
     */
    private void createPatterns(){
        redirectPattern = Pattern.compile("#REDIRECT"+"\\s*\\[\\[(.*?)\\]\\]", Pattern.CASE_INSENSITIVE);
    }
    /**
     * Default constructor. When no language is given, defaults to English.
     * @param wikiText the wiki text
     */
    public WikiTextParser(String wikiText){
        this(wikiText, "en");
    }

    public boolean isRedirect() { return redirect; }

    public String getRedirectText() { return redirectString; }

    public String getText() {
        return wikiText;
    }

    /**
     * Return only the unformatted text body. Heading markers are omitted.
     * @return the unformatted text body
     */
    public String getTextBody() {
        String text = getPlainText();
        text = stripBottomInfo(text, "See also");
        text = stripBottomInfo(text, "Further reading");
        text = stripBottomInfo(text, "References");
        text = stripBottomInfo(text, "Notes");
        text = cleanHeadings(text);
        return text;
    }

    /**
     * Strips any content following a specific heading, e.g. "See also", "References", "Notes", etc.
     * Everything following this heading (including the heading) is cut from the text.
     * @param text The wiki page text
     * @param label the heading label to cut
     * @return  the processed wiki text
     */
    private String stripBottomInfo(String text, String label) {
        Pattern bottomPattern = Pattern.compile("^=*\\s?" + label + "\\s?=*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = bottomPattern.matcher(text);
        if(matcher.find())
            text = text.substring(0, matcher.start());
        return text;
    }

    /**
     * Cleans the surrounding annotations on headings (e.g. "==" or "==="). Leaves the heading word intact.
     * @param text  the wiki text
     * @return  the processed text
     */
    private String cleanHeadings(String text) {
        Pattern startHeadingPattern = Pattern.compile("^=*", Pattern.MULTILINE);
        Pattern endHeadingPattern = Pattern.compile("=*$", Pattern.MULTILINE);
        text = startHeadingPattern.matcher(text).replaceAll("");
        text = endHeadingPattern.matcher(text).replaceAll("");
        return text;
    }

    public String getPlainText() {
        String text = wikiText.replaceAll("&gt;", ">");
        text = text.replaceAll("&lt;", "<");
        text = infoboxCleanupPattern.matcher(text).replaceAll(" ");
        text = commentsCleanupPattern.matcher(text).replaceAll(" ");
        text = stylesPattern.matcher(text).replaceAll(" ");
        text = refCleanupPattern.matcher(text).replaceAll(" ");
        text = text.replaceAll("</?.*?>", " ");
        text = curlyCleanupPattern0.matcher(text).replaceAll(" ");
        text = curlyCleanupPattern1.matcher(text).replaceAll(" ");
        text = cleanupPattern0.matcher(text).replaceAll(" ");

        Matcher m = cleanupPattern1.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            // For example: transform match to upper case
            int i = m.group().lastIndexOf('|');
            String replacement;
            if (i > 0) {
                replacement = m.group(1).substring(i - 1);
            } else {
                replacement = m.group(1);
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        text = sb.toString();

        text = text.replaceAll("'{2,}", "");
        return text.trim();
    }
}
