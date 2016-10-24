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

package com.creationgroundmedia.nytimessearch.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.creationgroundmedia.nytimessearch.R;
import com.creationgroundmedia.nytimessearch.adapters.ArticlesAdapter;
import com.creationgroundmedia.nytimessearch.models.Article;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

public class SearchActivity extends AppCompatActivity {

    private static final String QUERY_STRING = "query_string";
    private static final String LOG_TAG = SearchActivity.class.getSimpleName();
    private MenuItem mSearchMenu;
    private SearchView mSearchView;
    private ArrayList<Article> mArticles;
    private ArticlesAdapter mAdapter;
    private String mQueryString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mQueryString = savedInstanceState.getString(QUERY_STRING);
        }

        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mArticles = new ArrayList<>();
        RecyclerView rvArticles = (RecyclerView) findViewById(R.id.rv_articles);
        mAdapter = new ArticlesAdapter(this, mArticles);
        rvArticles.setAdapter(mAdapter);
        AutofitGridLayoutManager layoutManager = new AutofitGridLayoutManager(this, 500);
        rvArticles.setLayoutManager(layoutManager);
        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                doSearch(mQueryString, page);
            }
        });

        if (!TextUtils.isEmpty(mQueryString)) {
            doSearch(mQueryString, 0);
        }
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        mSearchMenu = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchMenu.getActionView();
        mSearchView.setQueryHint(getString(R.string.search_query_hint));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mQueryString = query;
                doSearch(query, 0);
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // we'll end up doing a brand new search (and losing our place) every time the user
        // rotates the device.  the right way to do this would be to persist all the search data
        // so far, and also the current position in the view.
        outState.putString(QUERY_STRING, mQueryString);
        super.onSaveInstanceState(outState);
    }

    private void doSearch(String query, final int page) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "a51f768c4a2146e393768e1758941ef3");
        params.put("page", page);
        params.put("q", query);
        String newsDesks = getNewsDesks();
        if (!TextUtils.isEmpty(newsDesks)) {
            // params.put("fq", "news_desk:(\"Fashion & Style\")");
            params.put("fq", "news_desk:(" + newsDesks + ")");
        }

        String sortOrder = getSortOrder();
        if (!TextUtils.isEmpty(sortOrder) && sortOrder.compareTo("relevance") != 0) {
            params.put("sort", sortOrder);
        }

        String beginDate = getBeginDate();
        if (!TextUtils.isEmpty(beginDate)) {
            params.put("begin_date", beginDate);
        }

        String endDate = getEndDate();
        if (!TextUtils.isEmpty(beginDate)) {
            params.put("end_date", endDate);
        }

        if (page == 0) {
            mArticles.clear();
            mAdapter.notifyDataSetChanged();
//            Log.d(LOG_TAG, "Starting page 0, adapter count = " + mAdapter.getItemCount());
        }

//        Log.d(LOG_TAG, "url: " + url);
//        Log.d(LOG_TAG, "params: " + params);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    mArticles.addAll(Article.fromJsonArray(articleJsonResults));
                    mAdapter.notifyDataSetChanged();
//                    Log.d(LOG_TAG, "After adding page " + page + ", adapter count = " + mAdapter.getItemCount());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }

    private String getNewsDesks() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> newsDesks = prefs.getStringSet(getString(R.string.pref_key_newsdesk), null);
//        Log.d(LOG_TAG, "newsdesks: " + newsDesks.toString());
        if (newsDesks == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : newsDesks) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(' ');
            }
            stringBuilder.append("\\\"");
            stringBuilder.append(str);
            stringBuilder.append("\\\"");
        }
        return stringBuilder.toString();
    }

    private String getSortOrder() {
        return getPrefString(R.string.pref_key_sortorder);
    }

    private String getBeginDate() {
        return getPrefString(R.string.pref_key_begindate);
    }

    private String getEndDate() {
        return getPrefString(R.string.pref_key_enddate);
    }

    private String getPrefString(String prefKey) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(prefKey, null);
    }

    private String getPrefString(int prefKey) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(getString(prefKey), null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public class AutofitGridLayoutManager extends GridLayoutManager {
        /**
         * the number of columns in the grid depends on how much width we've got to play with
         */
        private int mItemWidth;

        AutofitGridLayoutManager(Context context, int itemWidth) {
            super (context, 1);
            mItemWidth = itemWidth;
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            int spanCount = getWidth() / mItemWidth;
            if (spanCount < 1) {
                spanCount = 1;
            }
            setSpanCount(spanCount);
            super.onLayoutChildren(recycler, state);
        }
    }

    public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        // The minimum amount of items to have below your current scroll position
        // before loading more.
        private int visibleThreshold = 5;
        // The current offset index of data you have loaded
        private int currentPage = 0;
        // The total number of items in the dataset after the last load
        private int previousTotalItemCount = 0;
        // True if we are still waiting for the last set of data to load.
        private boolean loading = true;
        // Sets the starting page index
        private int startingPageIndex = 0;

        RecyclerView.LayoutManager mLayoutManager;

        public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
        }

        public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
            visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
        }

        public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
            visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
        }

        public int getLastVisibleItem(int[] lastVisibleItemPositions) {
            int maxSize = 0;
            for (int i = 0; i < lastVisibleItemPositions.length; i++) {
                if (i == 0) {
                    maxSize = lastVisibleItemPositions[i];
                }
                else if (lastVisibleItemPositions[i] > maxSize) {
                    maxSize = lastVisibleItemPositions[i];
                }
            }
            return maxSize;
        }

        // This happens many times a second during a scroll, so be wary of the code you place here.
        // We are given a few useful parameters to help us work out if we need to load some more data,
        // but first we check if we are waiting for the previous load to finish.
        @Override
        public void onScrolled(RecyclerView view, int dx, int dy) {
            int lastVisibleItemPosition = 0;
            int totalItemCount = mLayoutManager.getItemCount();

            if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
                // get maximum element within the list
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
            } else if (mLayoutManager instanceof LinearLayoutManager) {
                lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            } else if (mLayoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            }

            // If the total item count is zero and the previous isn't, assume the
            // list is invalidated and should be reset back to initial state
            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.loading = true;
                }
            }
            // If it’s still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            // number and total item count.
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
            }

            // If it isn’t currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            // threshold should reflect how many total columns there are too
            if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
                currentPage++;
                onLoadMore(currentPage, totalItemCount);
                loading = true;
            }
        }

        // Defines the process for actually loading more data based on page
        public abstract void onLoadMore(int page, int totalItemsCount);

    }

}
