/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.blocks;

/**
 * Created by yangzc on 17/2/13.
 */
public class CYParagraphStyle {

    private CYHorizontalAlign mHorizontalAlign = CYHorizontalAlign.LEFT;

    private int mTextColor;

    private int mTextSize;//base 640

    private int mMarginTop, mMarginBottom;

    public void setHorizontalAlign(CYHorizontalAlign align) {
        this.mHorizontalAlign = align;
    }

    public CYHorizontalAlign getHorizontalAlign() {
        return mHorizontalAlign;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setMarginTop(int marginTop) {
        this.mMarginTop = marginTop;
    }

    public int getMarginTop() {
        return mMarginTop;
    }

    public void setMarginBottom(int marginBottom) {
        this.mMarginBottom = marginBottom;
    }

    public int getMarginBottom() {
        return mMarginBottom;
    }
}
