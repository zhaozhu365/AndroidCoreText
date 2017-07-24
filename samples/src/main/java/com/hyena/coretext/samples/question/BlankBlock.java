/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples.question;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditBlock;
import com.hyena.coretext.blocks.CYEditFace;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/2/6.
 */
public class BlankBlock extends CYEditBlock {

    private boolean mIsRight = false;
    private String mClass = "choose";
    private String size;
    private int mWidth, mHeight;
    private static int DP_2 = Const.DP_1 * 2;
    private static int DP_3 = Const.DP_1 * 5;

    private String mDefaultText;
    public BlankBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init(content);
    }

    private void init(String content) {
        setPadding(DP_3, DP_2, DP_3, DP_2);
        try {
            JSONObject json = new JSONObject(content);
            setTabId(json.optInt("id"));
            this.mDefaultText = json.optString("default");
            this.size = json.optString("size");
            this.mClass = json.optString("class");//choose fillin
            if (getTextEnv().isEditable()) {
                if ("line".equals(size)) {
                    int dp20 = DP_2 * 10;
                    ((EditFace)getEditFace()).getTextPaint().setTextSize(dp20);
                    ((EditFace)getEditFace()).getDefaultTextPaint().setTextSize(dp20);
                    setAlignStyle(AlignStyle.Style_MONOPOLY);
                }
            }
            ((EditFace)getEditFace()).setClass(mClass);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateSize(getText());
    }

    @Override
    public String getDefaultText() {
        return mDefaultText;
    }

    @Override
    public void setText(String text) {
        updateSize(text);
        super.setText(text);
    }

    private void updateSize(String text) {
        if (text == null)
            text = "";
        int textHeight = getTextHeight(getTextEnv().getPaint());
        if (!getTextEnv().isEditable()) {
            int width = (int) getTextEnv().getPaint().measureText(text);
            this.mWidth = width + DP_2 * 5;
            this.mHeight = textHeight;
        } else {
            if ("letter".equals(size)) {
                this.mWidth = Const.DP_1 * 30;
                this.mHeight = Const.DP_1 * 50;
            } else if ("line".equals(size)) {
                this.mWidth = Const.DP_1 * 265;
                this.mHeight = getTextHeight(((EditFace)getEditFace()).getTextPaint());
            } else if ("express".equals(size)) {
                this.mWidth = Const.DP_1 * 50;
                this.mHeight = textHeight;
            } else {
                this.mWidth = Const.DP_1 * 50;
                this.mHeight = textHeight;
            }
        }
    }

    @Override
    public int getContentWidth() {
        return mWidth;
    }

    @Override
    public int getContentHeight() {
        return mHeight;
    }

    @Override
    protected CYEditFace createEditFace(TextEnv textEnv, ICYEditable editable) {
        return new EditFace(textEnv, editable);
    }
}
