/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples.question;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditBlock;
import com.hyena.coretext.blocks.CYEditFace;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.framework.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/2/6.
 */
public class BlankBlock extends CYEditBlock {

    private boolean mIsRight = false;
    private String mClass = "choose";
    private String size;

    public BlankBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init(content);
    }

    private void init(String content) {
        setPadding(UIUtils.dip2px(3), UIUtils.dip2px(2), UIUtils.dip2px(3), UIUtils.dip2px(2));
        try {
            JSONObject json = new JSONObject(content);
            setTabId(json.optInt("id"));
            setDefaultText(json.optString("default"));
            this.size = json.optString("size");
            this.mClass = json.optString("class");//choose fillin
            if (getTextEnv().isEditable()) {
                if ("line".equals(size)) {
                    getEditFace().getTextPaint().setTextSize(UIUtils.dip2px(20));
                    getEditFace().getDefaultTextPaint().setTextSize(UIUtils.dip2px(20));
                    setAlignStyle(AlignStyle.Style_MONOPOLY);
                }
            }
            ((EditFace)getEditFace()).setClass(mClass);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setText("测试");
    }

    @Override
    public int getContentWidth() {
        if (!getTextEnv().isEditable()) {
            int width = (int) getTextEnv().getPaint()
                    .measureText(getEditFace().getText());
            return width + UIUtils.dip2px(10);
        } else {
            if ("letter".equals(size)) {
                return UIUtils.dip2px(30);
            } else if ("line".equals(size)) {
                return UIUtils.dip2px(265);
            } else if ("express".equals(size)) {
                return UIUtils.dip2px(50);
            } else {
                return UIUtils.dip2px(50);
            }
        }
    }

    @Override
    public int getContentHeight() {
        int textHeight = getTextHeight(getTextEnv().getPaint());
        if (!getTextEnv().isEditable()) {
            return textHeight;
        } else {
            if ("letter".equals(size)) {
                return textHeight;
            } else if ("line".equals(size)) {
                return getTextHeight(getEditFace().getTextPaint());
            } else if ("express".equals(size)) {
                return textHeight;
            } else {
                return textHeight;
            }
        }
    }

    @Override
    protected CYEditFace createEditFace(TextEnv textEnv, ICYEditable editable) {
        return new EditFace(textEnv, editable);
    }
}
