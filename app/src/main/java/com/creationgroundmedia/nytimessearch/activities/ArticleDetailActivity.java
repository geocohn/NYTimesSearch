package com.creationgroundmedia.nytimessearch.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.creationgroundmedia.nytimessearch.R;
import com.creationgroundmedia.nytimessearch.models.Article;

import org.parceler.Parcels;

public class ArticleDetailActivity extends AppCompatActivity {
    private static final String ARTICLE = "article";
    private Article mArticle;

    public static void launchInstance(Context context, Article article) {
        context.startActivity(instanceIntent(context, article));
    }

    public static Intent instanceIntent(Context context, Article article) {
        Intent intent = new Intent(context, ArticleDetailActivity.class);
        intent.putExtra(ARTICLE, Parcels.wrap(article));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArticle = Parcels.unwrap(getIntent().getParcelableExtra(ARTICLE));

        setContentView(R.layout.activity_article_detail);

        TextView tvArticlde = (TextView) findViewById(R.id.tv_detail_article);
        tvArticlde.setText(mArticle.toString());
    }
}
