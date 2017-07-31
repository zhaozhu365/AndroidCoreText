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
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public CYImageBlock(TextEnv textEnv, String content){
        super(textEnv, content);
        mPaint.setColor(0xfff2f2f4);
        ImageLoader.getImageLoader().addImageFetcherListener(mImageFetcherListener);
    }

    public CYImageBlock setResId(Context context, int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        setBitmap(bitmap);
        LogUtil.v("yangzc", "setBitmap res: " + (bitmap == null));
        return this;
    }

    public CYImageBlock setDefaultBackGroundColor(int color) {
        mPaint.setColor(color);
        return this;
    }

    public CYImageBlock setResUrl(String url) {
        this.mUrl = url;
        if (TextUtils.isEmpty(url))
            return this;

        Bitmap bitmap = ImageLoader.getImageLoader().loadImage(url);
        if (bitmap != null) {
            LogUtil.v("yangzc", "setBitmap local: " + (bitmap == null));
            setBitmap(bitmap);
        }
        return this;
    }

    protected void setBitmap(Bitmap bitmap) {
        if (mBitmap != null && !mBitmap.isRecycled() && mBitmap == bitmap)
            return;

        if (bitmap != null && !bitmap.isRecycled()) {
            this.mBitmap = bitmap;
        }
        postInvalidate();
    }

    @Override
    public CYImageBlock setAlignStyle(AlignStyle style) {
        return (CYImageBlock) super.setAlignStyle(style);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mBitmap != null && !mBitmap.isRecycled()) {
            canvas.drawBitmap(mBitmap, null, getContentRect(), mPaint);
        } else {
            canvas.drawRect(getContentRect(), mPaint);
        }
    }

    @Override
    public void restart() {
        super.restart();
        ImageLoader.getImageLoader().addImageFetcherListener(mImageFetcherListener);
    }

    @Override
    public void stop() {
        super.stop();
        ImageLoader.getImageLoader().removeImageFetcherListener(mImageFetcherListener);
    }

    private ImageFetcher.ImageFetcherListener mImageFetcherListener = new ImageFetcher.ImageFetcherListener() {
        @Override
        public void onLoadComplete(String imageUrl, Bitmap bitmap, Object object) {
            if (!TextUtils.isEmpty(mUrl) && mUrl.equals(imageUrl)) {
                LogUtil.v("yangzc", "setBitmap net: " + (bitmap == null));
                setBitmap(bitmap);
            }
        }
    };

}
