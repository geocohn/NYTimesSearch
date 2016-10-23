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

package com.creationgroundmedia.nytimessearch.models;


import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by geo on 10/17/16.
 */

public class Article implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.webUrl);
        dest.writeString(this.snippet);
        dest.writeString(this.imageUrl);
        dest.writeString(this.headline);
    }

    protected Article(Parcel in) {
        this.webUrl = in.readString();
        this.snippet = in.readString();
        this.imageUrl = in.readString();
        this.headline = in.readString();
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
