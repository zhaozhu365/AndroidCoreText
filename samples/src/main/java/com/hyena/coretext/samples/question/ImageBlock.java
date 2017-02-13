/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples.question;

import android.content.Context;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYImageBlock;
import com.hyena.coretext.samples.R;
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
            setResUrl(context, url, R.drawable.baidu);
            String size = json.optString("size");
            if ("big_image".equals(size)) {
                setAlignStyle(AlignStyle.Style_MONOPOLY);
                mIsBigImage = true;
            } else {
                mIsBigImage = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getContentWidth() {
        if (mIsBigImage && mBitmap != null && !mBitmap.isRecycled()) {
            float scale = Math.min(mScreenWidth/mBitmap.getWidth(), MAX_HEIGHT / mBitmap.getHeight());
            return (int) (mBitmap.getWidth() * scale);
        }
        return super.getContentWidth();
    }

    @Override
    public int getContentHeight() {
        if (mIsBigImage && mBitmap != null && !mBitmap.isRecycled()) {
            float scale = Math.min(mScreenWidth/mBitmap.getWidth(), MAX_HEIGHT / mBitmap.getHeight());
            return (int) (mBitmap.getHeight() * scale);
        }
        return super.getContentHeight();
    }
}
