/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.blocks;

import com.hyena.coretext.TextEnv;

/**
 * Created by yangzc on 17/2/13.
 */
public class CYParagraphStyle {

    private String mStyleId;

    private CYHorizontalAlign mHorizontalAlign = CYHorizontalAlign.LEFT;

    private int mTextColor;

    private int mTextSize;//base 640

    private int mMarginTop, mMarginBottom;

    private String mStyle;

    private TextEnv mTextEnv;

    public CYParagraphStyle(TextEnv textEnv) {
        this.mTextEnv = textEnv;
    }

    public void setStyleId(String styleId) {
        this.mStyleId = styleId;
    }

    public String getStyleId() {
        return mStyleId;
    }

    public void setStyle(String style) {
        this.mStyle = style;
    }

    public String getStyle(){
        return mStyle;
    }

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
