/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples.question;

import android.graphics.Color;
import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYHorizontalAlign;
import com.hyena.coretext.blocks.CYParagraphStartBlock;
import com.hyena.coretext.blocks.CYParagraphStyle;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/2/14.
 */
public class ParagraphStartBlock extends CYParagraphStartBlock {

    public ParagraphStartBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init(content);
    }

    private void init(String content) {
        try {
            JSONObject json = new JSONObject(content);
            getStyle().setTextSize(json.optInt("size"));
            getStyle().setTextColor(Color.parseColor(json.optString("color")));
            getStyle().setMarginBottom(json.optInt("margin"));
            String align = json.optString("align");
            if ("left".equals(align) || TextUtils.isEmpty(align)) {
                getStyle().setHorizontalAlign(CYHorizontalAlign.LEFT);
            } else if("mid".equals(align)) {
                getStyle().setHorizontalAlign(CYHorizontalAlign.CENTER);
            } else {
                getStyle().setHorizontalAlign(CYHorizontalAlign.RIGHT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CYParagraphStyle getStyle() {
        return super.getStyle();
    }
}
