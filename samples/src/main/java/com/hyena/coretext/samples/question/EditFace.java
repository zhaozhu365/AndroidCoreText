/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples.question;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditFace;
import com.hyena.coretext.blocks.ICYEditable;

/**
 * Created by yangzc on 17/2/14.
 */
public class EditFace extends CYEditFace {

    private String mClass = "fillin";

    public EditFace(TextEnv textEnv, ICYEditable editable) {
        super(textEnv, editable);
    }

    public void setClass(String clazz) {
        this.mClass = clazz;
    }

    @Override
    protected void drawBorder(Canvas canvas, Rect blockRect) {
        if (!getTextEnv().isEditable())
            return;
        if ("choose".equals(mClass)) {
            super.drawBorder(canvas, blockRect);
        }
    }

    private RectF mRectF = new RectF();
    @Override
    protected void drawBackGround(Canvas canvas, Rect blockRect) {
        if (!getTextEnv().isEditable())
            return;

        mBackGroundPaint.setColor(Color.WHITE);
        mBackGroundPaint.setStyle(Paint.Style.FILL);
        mRectF.set(blockRect);
        if ("fillin".equals(mClass)) {
            canvas.drawRoundRect(mRectF, 10, 10, mBackGroundPaint);
        } else if ("choose".equals(mClass)) {
        }
    }

    @Override
    protected void drawFlash(Canvas canvas, Rect contentRect) {
        if (!getTextEnv().isEditable())
            return;

        mFlashPaint.setColor(0xff3eabff);
        if ("fillin".equals(mClass)) {
            super.drawFlash(canvas, contentRect);
        }
    }

    @Override
    public String getText() {
        if (getTextEnv().isEditable()) {
            return super.getText();
        } else {
            return "(" + super.getText() + ")";
        }
    }
}
