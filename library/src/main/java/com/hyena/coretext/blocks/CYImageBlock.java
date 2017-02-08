package com.hyena.coretext.blocks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import com.hyena.coretext.TextEnv;
import com.hyena.framework.utils.ImageFetcher;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYImageBlock extends CYPlaceHolderBlock {

    protected Bitmap mBitmap;

    public CYImageBlock(TextEnv textEnv, String content){
        super(textEnv, content);
    }

    public CYImageBlock setResId(Context context, int resId) {
        mBitmap = BitmapFactory.decodeResource(context.getResources(), resId);

        return this;
    }

    public CYImageBlock setResUrl(Context context, String url, int defaultResId) {
        Bitmap bitmap = ImageFetcher.getImageFetcher().getBitmapInCache(url);
        if (bitmap != null && !bitmap.isRecycled()) {
            this.mBitmap = bitmap;
            Log.v("yangzc", "get");
        } else {
            setResId(context, defaultResId);
            ImageFetcher.getImageFetcher().loadImage(url, url, new ImageFetcher.ImageFetcherListener() {
                @Override
                public void onLoadComplete(String imageUrl, Bitmap bitmap, Object object) {
                    if (bitmap != null && !bitmap.isRecycled()) {
                        mBitmap = bitmap;
                        requestLayout(true);
                        Log.v("yangzc", "load");
                    }
                }
            });
        }
        return this;
    }

    @Override
    public CYImageBlock setAlignStyle(AlignStyle style) {
        return (CYImageBlock) super.setAlignStyle(style);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mBitmap != null && !mBitmap.isRecycled()) {
            canvas.drawBitmap(mBitmap, null, getContentRect(), null);
        }
    }

    @Override
    public int getContentWidth() {
        if (mBitmap != null)
            return mBitmap.getWidth();
        return 100;
    }

    @Override
    public int getContentHeight() {
        if (mBitmap != null)
            return mBitmap.getHeight();
        return 100;
    }
}
