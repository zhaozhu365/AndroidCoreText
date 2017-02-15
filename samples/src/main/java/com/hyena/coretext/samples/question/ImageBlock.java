/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples.question;

import android.content.Context;
import android.graphics.Bitmap;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYImageBlock;
import com.hyena.coretext.samples.R;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/2/6.
 */
public class ImageBlock extends CYImageBlock {

    private static float MAX_HEIGHT = 0;
    private boolean mIsBigImage = false;
    private float mScreenWidth = 0;

    private int mWidth, mHeight;

    public ImageBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        MAX_HEIGHT = UIUtils.dip2px(100);
        mScreenWidth = textEnv.getContext().getResources().getDisplayMetrics().widthPixels;
        init(textEnv.getContext(), content);
    }

    private void init(Context context, String content) {
        try {
            JSONObject json = new JSONObject(content);
            String url = json.optString("src");
            String size = json.optString("size");
            LogUtil.v("yangzc", content);
            if ("big_image".equals(size)) {
                setAlignStyle(AlignStyle.Style_MONOPOLY);
                mIsBigImage = true;
                setWidth((int) mScreenWidth);
                setHeight(100);
            } else if ("small_img".equals(size)) {
                mIsBigImage = false;
                setWidth(UIUtils.dip2px(37));
                setHeight(UIUtils.dip2px(37));
            } else {
                mIsBigImage = false;
                setWidth(UIUtils.dip2px(60));
                setHeight(UIUtils.dip2px(60));
            }
            setResUrl(context, url, R.drawable.baidu);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setBitmap(Bitmap bitmap) {
        super.setBitmap(bitmap);
        if (mIsBigImage && mBitmap != null && !mBitmap.isRecycled()) {
            float scale = Math.min(mScreenWidth/mBitmap.getWidth(), MAX_HEIGHT / mBitmap.getHeight());
            setWidth((int) (mBitmap.getWidth() * scale));
            setHeight((int) (mBitmap.getHeight() * scale));
            getTextEnv().getEventDispatcher().requestLayout();
        }
        getTextEnv().getEventDispatcher().postInvalidate();
    }

}
