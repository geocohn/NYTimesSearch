package com.creationgroundmedia.nytimessearch;

import com.creationgroundmedia.nytimessearch.models.QueryResponse;
import com.creationgroundmedia.nytimessearch.models.api.ArticleSearch;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by geo on 10/18/16.
 */

public class ArticleSearchTest {
    private QueryResponse searchResult;

    @Test
    public void testArticleSearch() throws Exception {
        final CountDownLatch gate = new CountDownLatch(1);

        ArticleSearch articleSearch = new ArticleSearch();

        searchResult = null;
        articleSearch.search("android", 0, "a51f768c4a2146e393768e1758941ef3", new ArticleSearch.SearchResult() {
            @Override
            public void result(QueryResponse r) {
                searchResult = r;
                gate.countDown();
            }
        });

        gate.await(5, TimeUnit.SECONDS);
        assertNotNull(searchResult);
        assertNotEquals(searchResult.getResponse().getDocs(), "0");
    }
}
