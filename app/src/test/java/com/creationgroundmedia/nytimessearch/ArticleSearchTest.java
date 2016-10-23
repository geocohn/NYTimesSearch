/*
 * Copyright 2016 George Cohn III
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
