/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples.question;

import android.content.Context;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYImageBlock;
import com.hyena.coretext.utils.Const;
import com.hyena.framework.clientlog.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/2/6.
 */
public class ImageBlock extends CYImageBlock {

    private float mScreenWidth = 0;

    public ImageBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
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
                setWidth((int) mScreenWidth);
                setHeight((int) (mScreenWidth / 2));
            } else if ("small_img".equals(size)) {
                setWidth(Const.DP_1 * 37);
                setHeight(Const.DP_1 * 37);
            } else {
                setWidth(Const.DP_1 * 60);
                setHeight(Const.DP_1 * 60);
            }
            setResUrl(url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
