package com.example.android.newsfeed.Data;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

    public interface ArticleAdapterOnClickHandler {
        // Do something later
        //
    }
   // final private ArticleAdapterOnClickHandler mClickHandler;

    /**The Constructor */

    // later, please add a ArticleAdapterOnClickHandler! Thanks!
    public ArticleAdapter(Context context, List<Article> articleData) {
        mContext = context;
        mArticleData = articleData;
    }

    /** The View Holder Inner Class.
     * This will be used to inflate the layout
     * of the item in the recycler view*/

    public class ArticleAdapterViewHolder extends RecyclerView.ViewHolder {

        // The following variables represent the views
        // present on the recycler view's item layout.
        public ImageView  mArticleThumbnailIv;

        public TextView mArticleTitleTv;
        public TextView mArticleTrailTextTv;
        public TextView mTimePublishedTv;

        public ImageButton mSharingIconIb;

        // What is this about ?
        public ArticleAdapterViewHolder(@NonNull View view) {
            super(view);

            // Get the views from the list's item layout
            mArticleThumbnailIv = view.findViewById(R.id.article_image_Iv);
            mArticleTitleTv = view.findViewById(R.id.article_title_tv);
            mArticleTrailTextTv = view.findViewById(R.id.article_trailText_tv);
            mTimePublishedTv = view.findViewById(R.id.article_published_time_tv);
            mSharingIconIb = view.findViewById(R.id.sharing_icon);

            // view.setOnClickListener(this)

        }

        // define the onclick method here, please
    }

    /**
     * onCreateViewHolder is called by the RecyclerView to create a ViewHolder.
     * In this method, we inflate the layout defined for the view holder inside an xml file.
     * For our case, the viewholder's layout is called "news_list_item.xml"
     * The Layout is then returned to be placed inside the recycler view.
     *
     * @param viewGroup The ViewGroup into which the new View will be added after
     *                  it is bound to an adapter position.
     *
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
     *                               contents of the item at the given position in the data set.
     * @param position               The position of the item within the adapter's data set.
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

        // Set the time difference between the publication and the current time
        articleAdapterViewHolder.mTimePublishedTv
                .setText(getTheTimeAgo(currentArticle.getTimePublished(),"GMT"));

    }

    /**
     * This method is used to get the different between the time
     * an {@link Article} was published and the current time.
     *
     * It's will help us to determine how long ago the article
     * has been published. the result will be delivered in the format
     * "X timeUnit Ago". Ex : "5 minutes ago" or "30 seconds ago"
     *
     * @param completeDate represent the publication time
     * @param timeZone represent the time zone used on the publication time.
     *
     * @return A String that shows how long ago the article was published
     * */

    private String getTheTimeAgo (String completeDate, String timeZone) {

        // pUT THE FORMAT INSIDE OF A STRING RESSOURCE
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));

        try {
            // This will convert the "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" to a time in millisecond.
            // Which represent the difference, in millisecond, between the current date and the date / / 197

            long time = sdf.parse(completeDate).getTime();
            long now = System.currentTimeMillis();

            // Makes the difference between the publication time and the current time.
            // And format it to get the "time ago" format.
            // Ex: If an article was published 30 minutes from the current, the
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
     * This method is used to set the articles on a ArticleAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ArticleAdapter to display it.
     *
     * @param articleData The new article data to be displayed.
     */
    public void setArticleData (List<Article> articleData) {
        mArticleData = articleData;

        // This method will notify the recycler view
        // that a change of data occured.
        notifyDataSetChanged();
    }
}
