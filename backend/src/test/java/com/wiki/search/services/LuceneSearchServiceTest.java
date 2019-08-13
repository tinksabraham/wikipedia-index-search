/*
package com.wiki.search.services;

import com.wiki.search.models.WikiArticleModel;
import com.wiki.search.exception.IndexIOException;
import com.wiki.search.exception.IndexSearchParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;

*/
/**
 * @author Tinku Abraham
 *//*


@RunWith(MockitoJUnitRunner.class)
public class LuceneSearchServiceTest {

    @InjectMocks
    private LuceneSearchService luceneSearchServiceUnderTest;

    @Test(expected = IndexIOException.class)
    public void test_GivenSearchTermAndNoIndexDir_ThenThrowException() throws IndexIOException {

        // here we assume that there is no index directory at root level
        // i.e I provide a random value

        // Given
        final String searchTerm = "Gatsby";

        // When & Then
        luceneSearchServiceUnderTest.search(searchTerm);
    }

    @Test(expected = IndexSearchParseException.class)
    public void test_GivenSearchTermWithFrontWildCard_ThenThrowException() throws IndexSearchParseException {

        // Given
        final String searchTerm = "*Denniss";

        // When & Then
        luceneSearchServiceUnderTest.search(searchTerm);
    }

    @Test
    public void test_GivenSearchTermWithExactContributorName_ThenReturnCorrectArticle()  {

        // Given
        final String searchTerm = "Denniss";
        final String searchTermArticle = "Advanced Micro Devices";

        // When
        List<WikiArticleModel> wikiArticleModelList = luceneSearchServiceUnderTest.search(searchTerm);

        // Then
        assertEquals(wikiArticleModelList.get(0).getContributor(), searchTerm);
        assertEquals(wikiArticleModelList.get(0).getArticle(), searchTermArticle);

        // false is true
        assertNotEquals(wikiArticleModelList.get(0).getId(), "100");
        // true is true
        assertEquals(wikiArticleModelList.get(0).getId(), "2400");
    }

    @Test
    public void test_GivenSearchTermWithExactContributorNameWildCardEnd_ThenReturnCorrectArticle()  {

        // Given
        final String searchTerm = "Denniss*";

        // When
        List<WikiArticleModel> wikiArticleModelList = luceneSearchServiceUnderTest.search(searchTerm);

        // Then
        assertEquals(wikiArticleModelList.get(0).getContributor(), searchTerm.replace("*", ""));
        // true is true
        assertEquals(wikiArticleModelList.get(0).getId(), "2400");
    }

    @Test
    public void test_GivenSearchTerm_ThenCheckItContainsApollo()  {

        // Given
        final String searchTerm = "Apollo";

        // When
        List<WikiArticleModel> wikiArticleModelList = luceneSearchServiceUnderTest.search(searchTerm);
        boolean articleApollo13Exits = wikiArticleModelList.stream()
                .anyMatch(t -> t.getArticle().equals("Apollo 13"));

        boolean articleApollo9Exits = wikiArticleModelList.stream()
                .anyMatch(t -> t.getArticle().equals("Apollo 9"));

        // Then
        assertTrue(articleApollo13Exits);
        assertTrue(articleApollo9Exits);
    }

}*/
