package com.creationgroundmedia.nytimessearch.adapters;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.creationgroundmedia.nytimessearch.R;
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
        boolean hasPhoto = !TextUtils.isEmpty(article.getImageUrl());
        if (hasPhoto) {
            holder.ivImage.setVisibility(View.VISIBLE);
            holder.tvSnippet.setVisibility(View.GONE);
            Picasso.with(mContext)
                    .load(article.getImageUrl())
                    .centerCrop()
                    .fit()
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setVisibility(View.GONE);
            holder.tvSnippet.setVisibility(View.VISIBLE);
            holder.tvSnippet.setText(article.getSnippet());
        }

        holder.vItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCustomTabs(article.getWebUrl());
            }
        });
    }

    private void launchCustomTabs(String webUrl) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        builder.setActionButton(bitmapFromResource(R.drawable.ic_share_24dp),
                "Share Link",
                setupShareIntent(webUrl),
                true);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl((Activity) mContext, Uri.parse(webUrl));
    }

    private Bitmap bitmapFromResource(int resId) {
        Drawable sendIcon = ContextCompat.getDrawable(mContext, resId);
        Bitmap bitmap = Bitmap.createBitmap(sendIcon.getIntrinsicWidth(),
                sendIcon.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        sendIcon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        sendIcon.draw(canvas);
        return bitmap;
    }

    private PendingIntent setupShareIntent(String url) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        int requestCode = 100;
        return PendingIntent.getActivity(mContext,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
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
            tvSnippet = (TextView) itemView.findViewById(R.id.tvSnippet);
            ivImage = (ImageView) itemView.findViewById(R.id.ivthumb);
        }
    }
}
