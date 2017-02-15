package com.hyena.coretext.blocks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.utils.ImageLoader;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.ImageFetcher;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYImageBlock extends CYPlaceHolderBlock {

    protected Bitmap mBitmap;
    private String mUrl;

    public CYImageBlock(TextEnv textEnv, String content){
        super(textEnv, content);
        ImageLoader.getImageLoader().addImageFetcherListener(mImageFetcherListener);
    }

    public CYImageBlock setResId(Context context, int resId) {
        mBitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        setBitmap(mBitmap);
        LogUtil.v("yangzc", "setBitmap res: " + (mBitmap == null));
        return this;
    }

    public CYImageBlock setResUrl(Context context, String url, int defaultResId) {
        this.mUrl = url;
        Bitmap bitmap = ImageLoader.getImageLoader().getImageLoader().loadImage(url);
        if (bitmap != null && !bitmap.isRecycled()) {
            LogUtil.v("yangzc", "setBitmap local: " + (bitmap == null));
            setBitmap(bitmap);
        } else {
            setResId(context, defaultResId);
        }
        return this;
    }

    protected void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        getTextEnv().getEventDispatcher().postInvalidate();
    }

    @Override
    public CYImageBlock setAlignStyle(AlignStyle style) {
        return (CYImageBlock) super.setAlignStyle(style);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        LogUtil.v("yangzc", "drawBitmap: " + (mBitmap == null));
        if (mBitmap != null && !mBitmap.isRecycled()) {
            canvas.drawBitmap(mBitmap, null, getContentRect(), getTextEnv().getPaint());
        } else {
            canvas.drawRect(getContentRect(), getTextEnv().getPaint());
        }
    }

    @Override
    public void release() {
        super.release();
        ImageLoader.getImageLoader().getImageLoader().removeImageFetcherListener(mImageFetcherListener);
    }

    private ImageFetcher.ImageFetcherListener mImageFetcherListener = new ImageFetcher.ImageFetcherListener() {
        @Override
        public void onLoadComplete(String imageUrl, Bitmap bitmap, Object object) {
            if (!TextUtils.isEmpty(mUrl) && mUrl.equals(imageUrl) && bitmap != null && !bitmap.isRecycled()) {
                LogUtil.v("yangzc", "setBitmap net: " + (bitmap == null));
                setBitmap(bitmap);
            }
        }
    };

}
