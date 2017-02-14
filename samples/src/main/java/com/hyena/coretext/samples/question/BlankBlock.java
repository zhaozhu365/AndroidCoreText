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

    public BlankBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init(content);
    }

    private void init(String content) {
        setPadding(UIUtils.dip2px(3), getPaddingTop(), UIUtils.dip2px(3), getPaddingBottom());
        try {
            JSONObject json = new JSONObject(content);
            setTabId(json.optInt("id"));
            setDefaultText(json.optString("default"));
            String size = json.optString("size");
            if (!getTextEnv().isEditable()) {
                int height = (int) (Math.ceil(getTextEnv().getPaint().descent()
                        - getTextEnv().getPaint().ascent()) + 0.5f);
                int width = (int) getTextEnv().getPaint().measureText(getEditFace().getText());
                setWidth(width + UIUtils.dip2px(10));
                setHeight(height);
            } else {
                if ("letter".equals(size)) {
                    //
                    setWidth(UIUtils.dip2px(30));
                } else if ("line".equals(size)) {
                    setWidth(UIUtils.dip2px(265) + getPaddingLeft() + getPaddingRight());
                    setHeight(UIUtils.dip2px(55) + getPaddingTop() + getPaddingBottom());
                    setAlignStyle(AlignStyle.Style_MONOPOLY);
                } else if ("express".equals(size)) {
                    setWidth(UIUtils.dip2px(50));
                    setHeight(UIUtils.dip2px(55));
                } else {
                    setWidth(UIUtils.dip2px(50));
                    setHeight(UIUtils.dip2px(55));
                }
            }

            this.mClass = json.optString("class");//choose fillin
            ((EditFace)getEditFace()).setClass(mClass);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected CYEditFace createEditFace(TextEnv textEnv, ICYEditable editable) {
        return new EditFace(textEnv, editable);
    }
}
