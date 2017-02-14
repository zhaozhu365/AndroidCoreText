/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.blocks;

import com.hyena.coretext.TextEnv;

/**
 * Created by yangzc on 17/2/13.
 */
public class CYParagraphStartBlock extends CYBlock {

    private CYParagraphStyle mStyle;

    public CYParagraphStartBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        mStyle = new CYParagraphStyle();
    }

    @Override
    public int getContentWidth() {
        return 0;
    }

    @Override
    public int getContentHeight() {
        return 0;
    }

    public CYParagraphStyle getStyle() {
        return mStyle;
    }
}
