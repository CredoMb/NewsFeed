package com.example.android.newsfeed.Data;

import android.content.Context;
import android.content.AsyncTaskLoader;
import com.example.android.newsfeed.Article;
import java.util.List;

public class ArticleLoader extends AsyncTaskLoader<List<Article>>{

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = ArticleLoader.class.getName();

    /**
     * Query Url
     **/
    private String mUrl;

    /**
     * The constructor
     */
    public ArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    /**
     * This will help load the data
     */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This will cause the "QueryUtils.fetchArticlesData" to be executed on a background
     * thread
     */
    @Override
    public List<Article> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Make the network request and
        // return a list of article

        return QueryUtils.fetchArticlesData(mUrl);
    }

}
