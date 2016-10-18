package com.creationgroundmedia.nytimessearch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.creationgroundmedia.nytimessearch.R;
import com.creationgroundmedia.nytimessearch.activities.ArticleDetailActivity;
import com.creationgroundmedia.nytimessearch.models.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by geo on 10/11/16.
 */

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    static final String LOG_TAG = ArticlesAdapter.class.getSimpleName();
    private Context mContext;
    private List<Article> mArticles;

    private Context getContext() {
        return mContext;
    }

    public ArticlesAdapter(Context context, ArrayList<Article> articles) {
        mContext = context;
        mArticles = articles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(getContext())
                .inflate(R.layout.content_search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Article article = mArticles.get(position);
        holder.tvHeadline.setText(article.getHeadline());
        if (!TextUtils.isEmpty(article.getImageUrl())) {
            Picasso.with(mContext).load(article.getImageUrl()).into(holder.ivImage);
        }

        holder.vItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticleDetailActivity.launchInstance(mContext, article);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View vItem;
        TextView tvHeadline;
        TextView tvSnippet;
        ImageView ivImage;

        ViewHolder(View itemView) {
            super(itemView);

            vItem = itemView;

            tvHeadline = (TextView) itemView.findViewById(R.id.tvHeadline);
            ivImage = (ImageView) itemView.findViewById(R.id.ivthumb);
        }
    }
}
