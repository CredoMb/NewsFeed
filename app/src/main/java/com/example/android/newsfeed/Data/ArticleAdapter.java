package com.example.android.newsfeed.Data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.newsfeed.Article;
import com.example.android.newsfeed.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleAdapterViewHolder> {

    private List<Article> mArticleData = new ArrayList<Article>();
    private Context mContext;
    final private ArticleAdapterOnClickHandler mClickHandler;
    private final String SHARE_ARTICLE = "Share Article";

    public interface ArticleAdapterOnClickHandler {
        void OnClick(int position);
    }

    /**
     * The Constructor
     */

    public ArticleAdapter(Context context, List<Article> articleData, ArticleAdapterOnClickHandler clickHandler) {
        mContext = context;
        mArticleData = articleData;
        mClickHandler = clickHandler;
    }

    /**
     * The View Holder Inner Class.
     * This will be used to store the views
     * of the item's layout inside variables.
     */

    public class ArticleAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // The following variables represent the views
        // present on the recycler view's item layout.
        // They will store each one of the view of the item's layout.

        public ImageView mArticleThumbnailIv;

        public TextView mArticleTitleTv;
        public TextView mArticleTrailTextTv;
        public TextView mTimePublishedTv;

        public ImageButton mSharingIconIb;
        public View mSeparatorView;

        /**
         * The View Holder Constructor .
         * Will get each view of the item's layout
         * and store it inside the corresponding variable.
         * <p>
         * The views of the item layout are retrieved
         * through their Id.
         * <p>
         * Ex: The "article image view" will be stored
         * inside "mArticleThumbnailIv"
         *
         * @param view will be passed to the constructor by
         *             "onCreateViewHolder". It represents the
         *             layout of the list's item.
         */

        public ArticleAdapterViewHolder(@NonNull View view) {
            super(view);

            // Get the views from the list's item layout
            mArticleThumbnailIv = view.findViewById(R.id.article_image_Iv);
            mArticleTitleTv = view.findViewById(R.id.article_title_tv);
            mArticleTrailTextTv = view.findViewById(R.id.article_trailText_tv);
            mTimePublishedTv = view.findViewById(R.id.article_published_time_tv);
            mSharingIconIb = view.findViewById(R.id.sharing_icon);

            // This will handle the sharing process.
            // Whenever the sharing icon is clicked,
            // the app will create a sharing intent
            // with a text made of the title and the link for the
            // web page.
            mSharingIconIb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Store the article Title and link inside 2 separated
                    // variables, one for each of them.
                    String articleTitle = mArticleData.get(getAdapterPosition()).getTitle();
                    String articleLink = mArticleData.get(getAdapterPosition()).getFullArticleUrl();

                    // Create a String with a concatenation of the
                    // article's title and link. This will be shared
                    // though an intent as the article summary.
                    String articleSharingText = articleTitle + "\n"
                            + articleLink;

                    /*
                     * Will help to share the article summary
                     * as an intent
                     * */
                    ShareCompat.IntentBuilder
                            .from((Activity) mContext)
                            .setType("text/plain")
                            .setChooserTitle(SHARE_ARTICLE)
                            .setText(articleSharingText)
                            .startChooser();

                }
            });
            mSeparatorView = view.findViewById(R.id.line_separator);

            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            mClickHandler.OnClick(getAdapterPosition());
        }
    }

    /**
     * onCreateViewHolder is called by the RecyclerView to create a ViewHolder.
     * In this method, we inflate the layout defined for the view holder inside an xml file.
     * For our case, the viewholder's layout is called "news_list_item.xml"
     * The Layout is then returned to be placed inside the recycler view.
     *
     * @param viewGroup The ViewGroup into which the new View will be added after
     *                  it is bound to an adapter position.
     * @param viewType  The view type of the new View.
     */

    @NonNull
    @Override
    public ArticleAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.news_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ArticleAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the article
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param articleAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                 contents of the item at the given position in the data set.
     * @param position                 The position of the item within the adapter's data set.
     */

    @Override
    public void onBindViewHolder(@NonNull ArticleAdapterViewHolder articleAdapterViewHolder, int position) {

        Article currentArticle = mArticleData.get(position);

        GlideHelperClass glideHelper = new GlideHelperClass(mContext,
                currentArticle.getThumbnailUrl(),
                R.drawable.placeholder_image,
                articleAdapterViewHolder.mArticleThumbnailIv);

        // Log.e("the thumbnail link",currentArticle.getThumbnailUrl());
        glideHelper.loadImage();

        // Set the title and the trailText of the article
        articleAdapterViewHolder.mArticleTitleTv.setText(currentArticle.getTitle());
        articleAdapterViewHolder.mArticleTrailTextTv.setText(currentArticle.getTrailText());

        Log.e("the textView color",
                String.valueOf(articleAdapterViewHolder.mArticleTrailTextTv.getTextColors()));

        // Set the time difference between the publication and the current time
        articleAdapterViewHolder.mTimePublishedTv
                .setText(getTheTimeAgo(currentArticle.getTimePublished(), "GMT"));
    }

    /**
     * This method is used to get the different between the time
     * an {@link Article} was published and the current time.
     * <p>
     * It's will help us to determine how long ago the article
     * has been published. the result will be delivered in the format
     * "X timeUnit Ago". Ex : "5 minutes ago" or "30 seconds ago"
     *
     * @param completeDate represent the publication time
     * @param timeZone     represent the time zone used on the publication time.
     * @return A String that shows how long ago the article was published
     */

    private String getTheTimeAgo(String completeDate, String timeZone) {

        // pUT THE FORMAT INSIDE OF A STRING RESSOURCE
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));

        try {
            // This will convert the "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" to a time in millisecond.
            // Which represent the difference, in millisecond, between the current date and the date
            // January 1, 1970, 00:00:00 GMT
            long time = sdf.parse(completeDate).getTime();
            long now = System.currentTimeMillis();

            // Makes the difference between the publication time and the current time.
            // And format it to get the "time ago" format.
            // Ex: If an article was published 30 minutes from the current time, the
            // ago CharSequence will contain "30 minutes ago"
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS);
            return ago.toString();

            // If the operation above didn't work, catch and display the exception
        } catch (
                ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our list of articles
     */

    @Override
    public int getItemCount() {
        if (mArticleData == null) {
            return 0;
        }
        return mArticleData.size();
    }

    /**
     * This method is used to get the list of articles.
     * This is handy when we need to get certain information about
     * a specific item in the list.
     * <p>
     * Ex: To get the link to the article so we can create a
     * Web Intent.
     */

    public List<Article> getArticleData() {
        return mArticleData;
    }

    /**
     * This method is used to set the articles on a ArticleAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ArticleAdapter to display it.
     *
     * @param articleData The new article data to be displayed.
     */
    public void setArticleData(List<Article> articleData) {
        mArticleData = articleData;

        // This method will notify the recycler view
        // that a change of data occured.
            notifyDataSetChanged();
    }

}
