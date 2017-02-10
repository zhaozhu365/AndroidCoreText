/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples.question;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditBlock;
import com.hyena.framework.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/2/6.
 */
public class BlankBlock extends CYEditBlock {

    private boolean mIsRight = false;

    public BlankBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init(content);
    }

    private void init(String content) {
        setWidth(UIUtils.dip2px(50));
        setPadding(UIUtils.dip2px(3), getPaddingTop(), UIUtils.dip2px(3), getPaddingBottom());
//        setHintPadding(UIUtils.dip2px(3));
        setText("填空");
//        mInputHintPaint.setStrokeWidth(UIUtils.dip2px(2));
        try {
            JSONObject json = new JSONObject(content);
            setTabId(json.optInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Canvas canvas) {
//        if (getTextEnv().isEditable()) {
            super.draw(canvas);
//        } else {
//            //draw answer
//            Rect contentRect = getContentRect();
//            if (!TextUtils.isEmpty(getText())) {
//                float textX;
//                float textWidth = mTextPaint.measureText(getText());
//                if (textWidth > contentRect.width()) {
//                    textX = contentRect.right - textWidth;
//                } else {
//                    textX = contentRect.left + (contentRect.width() - textWidth)/2;
//                }
//                mTextPaint.setColor(Color.BLACK);
//                Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
//                canvas.drawText(getText(), textX, contentRect.bottom - fontMetrics.bottom, mTextPaint);
//            }
//
//            mTextPaint.setColor(Color.RED);
//            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
//            canvas.drawText("(", getBlockRect().left, contentRect.bottom - fontMetrics.bottom, mTextPaint);
//            canvas.drawText(")", getContentRect().right, contentRect.bottom - fontMetrics.bottom, mTextPaint);
//        }
    }

    @Override
    public boolean isDebug() {
        return false;
    }
}
