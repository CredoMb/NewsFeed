package com.example.android.newsfeed;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable {

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

    /** Private constructor to be used
     *  by the createFromParcel method of
     *  Parcelable Creator. Will create an Article object from the
     *  Parcel*/

    private Article (Parcel in) {

        this( in.readString(), // The title
                in.readString(), // The TrailText
                in.readString(), // The ThumbnailUrl Url
                in.readString(), // The Full Article Url
                in.readString()); // The Publication Time

    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**Will be used to turn The Article Object into a parcel.*/
    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(mTitle); // Title
        parcel.writeString(mTrailText); // The Trail Text
        parcel.writeString(mThumbnailUrl); // The Thumbnail Url
        parcel.writeString(mFullArticleUrl); // The Url of the Full Article
        parcel.writeString(mTimePublished); // The Publication time
    }

    /** Will help us generate an instance of
     *  "AMovie" from a Parcel,
     *  by using the constructor "Article(Parcel in)". */

    public final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel parcel) {
            return new Article(parcel);
        }

        @Override
        public Article[] newArray(int i) {
            return new Article[i];
        }

    };

    // Getters of the class.
    // They are used inside the Loader

    public String getTitle () {return mTitle;}
    public String getTrailText () {return mTrailText;}
    public String getThumbnailUrl () {return mThumbnailUrl;}
    public String getFullArticleUrl () {return mFullArticleUrl;}
    public String getTimePublished () {return mTimePublished;}

}
