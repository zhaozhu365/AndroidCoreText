/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.utils;

import android.graphics.Bitmap;

import com.hyena.framework.utils.ImageFetcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 17/2/15.
 */
public class ImageLoader {

    private static ImageLoader mImageLoader;
    private List<ImageFetcher.ImageFetcherListener> mFetcherListeners;

    private ImageLoader() {}

    public static ImageLoader getImageLoader() {
        if (mImageLoader == null)
            mImageLoader = new ImageLoader();
        return mImageLoader;
    }

    public Bitmap loadImage(String url) {
        Bitmap bitmap = ImageFetcher.getImageFetcher().getBitmapInCache(url);
        if (bitmap != null && !bitmap.isRecycled()) {
            return bitmap;
        } else {
            ImageFetcher.getImageFetcher().loadImage(url, url, mFetcherListener);
        }
        return null;
    }

    public void addImageFetcherListener(ImageFetcher.ImageFetcherListener listener) {
        if (mFetcherListeners == null)
            mFetcherListeners = new ArrayList<ImageFetcher.ImageFetcherListener>();
        if (!mFetcherListeners.contains(listener)) {
            mFetcherListeners.add(listener);
        }
    }

    public void removeImageFetcherListener(ImageFetcher.ImageFetcherListener listener) {
        if (mFetcherListeners == null)
            return;
        mFetcherListeners.remove(listener);
    }

    private ImageFetcher.ImageFetcherListener mFetcherListener
            = new ImageFetcher.ImageFetcherListener() {
        @Override
        public void onLoadComplete(String imageUrl, Bitmap bitmap, Object object) {
            if (mFetcherListeners != null) {
                for (int i = 0; i < mFetcherListeners.size(); i++) {
                    ImageFetcher.ImageFetcherListener listener = mFetcherListeners.get(i);
                    listener.onLoadComplete(imageUrl, bitmap, object);
                }
            }
        }
    };
}
