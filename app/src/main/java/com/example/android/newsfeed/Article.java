package com.example.android.newsfeed;

public class Article {

    private String mTitle;
    private String mTrailText;
    private String mThumbnailUrl;
    private String mFullArticleUrl;
    private String mTimePublished;

    // The class' constructor
    public Article (String title, String trailText,
                    String thumbnail, String fullArticleUrl,
                    String timePublished) {

        mTitle =title;
        mTrailText = trailText;
        mThumbnailUrl = thumbnail;
        mFullArticleUrl = fullArticleUrl;
        mTimePublished = timePublished;
    }

    // Getters of the class.
    // They are used inside the Loader

    public String getTitle () {return mTitle;}
    public String getTrailText () {return mTrailText;}
    public String getThumbnailUrl () {return mThumbnailUrl;}
    public String getFullArticleUrl () {return mFullArticleUrl;}
    public String getTimePublished () {return mTimePublished;}

}
