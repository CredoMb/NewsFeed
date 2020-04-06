package com.example.android.newsfeed.Data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class GlideHelperClass extends AppGlideModule {

    private Context mContext;
    private String mImageLink;
    private int mPlaceHolderId;
    private ImageView mTargetIv;

    public GlideHelperClass(Context c, String imageLink, int placeHolderId,
                            ImageView targetIv) {

        mContext = c;
        mImageLink = imageLink;
        mPlaceHolderId = placeHolderId;
        mTargetIv = targetIv;
        // mErrorImageId = errorId;
    }
    public void loadImage() {

            Glide.with(mContext)
                    .load(mImageLink).apply(RequestOptions.centerCropTransform())
                    .apply(new RequestOptions().placeholder(mPlaceHolderId))
                    .into(mTargetIv)
                    ;
            //.error(mErrorImageId)

    }
}
