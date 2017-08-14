package com.hyena.coretext.blocks;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.utils.Const;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.ImageFetcher;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYImageBlock extends CYPlaceHolderBlock implements ImageLoadingListener {

    private String mUrl = "";
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private DisplayImageOptions options;
    protected Drawable drawable = null;
    protected ImageAware mImageAware;

    public CYImageBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        ImageFetcher.getImageFetcher();
        init();
    }

    private void init() {
        mPaint.setColor(0xffe9f0f6);
        mPaint.setStrokeWidth(Const.DP_1);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public void loadImage(String url, int width, int height,
                          int failResId, int emptyResId, int loadingResId) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheInMemory(true);
        builder.cacheOnDisk(true);
        builder.showImageOnFail(failResId);
        builder.showImageForEmptyUri(emptyResId);
        builder.showImageOnLoading(loadingResId);
        mImageAware = new ThisImageAware(width, height);
        ImageLoader.getInstance().displayImage(url, mImageAware, options = builder.build(), this);
    }

    @Override
    public boolean onTouchEvent(int action, float x, float y) {
        super.onTouchEvent(action, x, y);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                //action click
                retry();
                break;
            }
        }
        return super.onTouchEvent(action, x, y);
    }

    @Override
    public void draw(Canvas canvas) {
        tryLoadFromCache();
        if (drawable != null) {
            Rect rect= getContentRect();
            if (drawable.getIntrinsicWidth() > 0 && drawable.getIntrinsicHeight() > 0) {
                if (rect.width() * drawable.getIntrinsicHeight() > rect.height() * drawable.getIntrinsicWidth()) {
                    //按照图片的高度缩放
                    int width = (int) (rect.height() * 1.0f * drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight());
                    mImageRect.set(rect.left + (rect.width() - width) / 2, rect.top, rect.right - (rect.width() - width) / 2, rect.bottom);
                } else {
                    //按照图片的宽度缩放
                    int height = (int) (rect.width() * 1.0f * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth());
                    mImageRect.set(rect.left, rect.top + (rect.height() - height) / 2, rect.right, rect.bottom - (rect.height() - height) / 2);
                }
            } else {
                mImageRect.set(rect);
            }
            drawable.setBounds(mImageRect);
            drawable.draw(canvas);
        }
    }

    private boolean isSuccess = false;
    private void tryLoadFromCache() {
        if (isSuccess || mImageAware == null)
            return;
        String key = mUrl + "_" + mImageAware.getWidth() + "x" + mImageAware.getHeight();
        Bitmap bitmap = com.nostra13.universalimageloader.core.ImageLoader.getInstance().getMemoryCache().get(key);
        if (bitmap != null && !bitmap.isRecycled()) {
            isSuccess = true;
            drawable = new BitmapDrawable(getTextEnv().getContext()
                    .getResources(), bitmap);
        } else {
            isSuccess = false;
        }
    }

    public void retry() {
        if (TextUtils.isEmpty(mUrl) || drawable != null || mImageAware == null) {
            return;
        }
        ImageLoader.getInstance().displayImage(mUrl, mImageAware, options, this);
    }

    @Override
    public void restart() {
        super.restart();
        retry();
    }

    @Override
    public void stop() {
        super.stop();
    }

    private Rect mImageRect = new Rect();
    /* ImageAware实现 */
    private class ThisImageAware implements ImageAware {
        private int width, height;
        public ThisImageAware(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public int getWidth() {
            return (int) (width * getScale());
        }

        @Override
        public int getHeight() {
            return (int) (height * getScale());
        }

        private float getScale() {
            int screenHeight = getTextEnv().getContext().getResources()
                    .getDisplayMetrics().heightPixels;
            float scale = 1.0f;
            if (height > screenHeight) {
                scale = screenHeight * 1.0f/height;
            }
            return scale;
        }

        @Override
        public ViewScaleType getScaleType() {
            return ViewScaleType.FIT_INSIDE;
        }

        @Override
        public View getWrappedView() {
            return null;
        }

        @Override
        public boolean isCollected() {
            return false;
        }

        @Override
        public int getId() {
            return TextUtils.isEmpty(mUrl)?super.hashCode():mUrl.hashCode();
        }

        private void setImageDrawableInfo(Drawable drawable) {
            CYImageBlock.this.drawable = drawable;
            postInvalidate();
        }

        @Override
        public boolean setImageDrawable(Drawable drawable) {
            if (drawable != null)
                setImageDrawableInfo(drawable);
            return true;
        }

        @Override
        public boolean setImageBitmap(Bitmap bitmap) {
            setImageDrawableInfo(new BitmapDrawable(getTextEnv().getContext()
                    .getResources(), bitmap));
            return true;
        }
    };

    public boolean isSuccess() {
        return isSuccess;
    }

    /* 回调部分 */
    @Override
    public void onLoadingStarted(String s, View view) {
        LogUtil.v("yangzc", "onLoadingStarted: " + s);
    }

    @Override
    public void onLoadingFailed(String s, View view, FailReason failReason) {
        LogUtil.v("yangzc", "onLoadingFailed: " + s);
    }

    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
        LogUtil.v("yangzc", "onLoadingComplete: " + s);
    }

    @Override
    public void onLoadingCancelled(String s, View view) {
        LogUtil.v("yangzc", "onLoadingCancelled: " + s);
    }

}
