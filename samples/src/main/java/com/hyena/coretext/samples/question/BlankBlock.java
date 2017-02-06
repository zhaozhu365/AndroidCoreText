/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples.question;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditBlock;
import com.hyena.framework.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/2/6.
 */
public class BlankBlock extends CYEditBlock {

    private int mID = 0;
    private boolean mIsRight = false;

    public BlankBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init(content);
    }

    private void init(String content) {
        Paint paint = getTextEnv().getPaint();
        int height = (int) (Math.ceil(paint.descent() - paint.ascent()) + 0.5f);
        setWidth(UIUtils.dip2px(80));
        setHeight(height);
        try {
            JSONObject json = new JSONObject(content);
            this.mID = json.optInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return mID;
    }

    @Override
    public void draw(Canvas canvas) {
        if (getTextEnv().isEditable()) {
            super.draw(canvas);
        } else {
            //draw answer
        }
    }
}
