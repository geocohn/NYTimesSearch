package com.creationgroundmedia.nytimessearch.models;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by geo on 10/17/16.
 */

@Parcel
public class Article {
    String webUrl;
    String snippet;
    String imageUrl;
    String headline;

    public Article() {
    }

    public String getHeadline() {
        return headline;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public Article(JSONObject jsonObject) {
        try {
            webUrl = jsonObject.getString("web_url");
            snippet = jsonObject.getString("snippet");
            headline = jsonObject.getJSONObject("headline").getString("main");
            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if (multimedia.length() > 0) {
                imageUrl = "http://www.nytimes.com/" + ((JSONObject) multimedia.getJSONObject(0)).getString("url");
            } else {
                imageUrl = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJsonArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new Article(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    @Override
    public String toString() {
        return "Article: " + getHeadline() + getSnippet();
    }
}
