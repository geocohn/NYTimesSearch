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
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.creationgroundmedia.nytimessearch.R;

import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by George Cohn III on 10/19/16.
 * Custom dialog for dates using a DatePicker
 */

public class DatePreference extends DialogPreference {
    private static String LOG_TAG = DatePreference.class.getSimpleName();
    private final Context mContext;
    private DatePicker mDatePicker;
    private final String mKey;
    private TextView mTvDisplayDate;

    public DatePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mKey = extractString(attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "key"));
//        Log.d(LOG_TAG, "constructor, key = " + mKey);
    }

    private String extractString(String str) {
        // had to do this because we don't know if the xml contains a hard coded string
        // or a reference to strings.xml
        if (!TextUtils.isEmpty(str)) {
            if (str.charAt(0) == '@') {
                str = mContext.getString(Integer.parseInt(str.substring(1)));
            }
        }
        return str;
    }


    @Override
    protected void onBindView(View view) {
//        Log.d(LOG_TAG, "onBindView()");
        mTvDisplayDate = (TextView) view.findViewById(R.id.display_date);
        String date = getPref();
        if (!TextUtils.isEmpty(date)) {
            mTvDisplayDate.setText(displayDate(getYear(date), getMonth(date), getDay(date)));
        }
        super.onBindView(view);
    }

    @Override
    protected void onBindDialogView(View view) {
//        Log.d(LOG_TAG, "onBindDialogView()");
        mDatePicker = (DatePicker) view.findViewById(R.id.date_picker);
        String date = getPref();
        if (!TextUtils.isEmpty(date)) {
            mDatePicker.init(getYear(date), getMonth(date), getDay(date), null);
        }
        super.onBindDialogView(view);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
//        Log.d(LOG_TAG, "onCreateView()");
        // we want a negative response to cause the date to be cleared, not simply reverted
        setNegativeButtonText("Clear");
        return super.onCreateView(parent);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
//        Log.d(LOG_TAG, "onDialogClosed(" + positiveResult + ")");
        if (positiveResult) {
            int year = mDatePicker.getYear();
            int month = mDatePicker.getMonth() + 1;
            int day = mDatePicker.getDayOfMonth();
            setPref(apiDate(year, month, day));
            mTvDisplayDate.setText(displayDate(year, month, day));
        } else {
            setPref(null);
            mTvDisplayDate.setText(null);
        }
        super.onDialogClosed(positiveResult);
    }

    private String displayDate(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    private String apiDate(int year, int month, int day) {
        return String.format("%04d%02d%02d", year, month, day);
    }

    private String getPref() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String date = prefs.getString(mKey, null);
//        Log.d(LOG_TAG, "getPref got " + date);
        return date;
    }
    private void setPref(String date) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit();
        editor.putString(mKey, date);
        editor.commit();
//        Log.d(LOG_TAG, "setPref(" + date + ")");
    }

    private int getDay(@NonNull String date) {
        return Integer.parseInt(date.substring(6,8));
    }

    private int getMonth(@NonNull String date) {
        return Integer.parseInt(date.substring(4,6)) - 1;
    }

    private int getYear(@NonNull String date) {
        return Integer.parseInt(date.substring(0,4));
    }
}