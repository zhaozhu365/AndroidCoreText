/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.builder.CYBlockProvider;
import com.hyena.coretext.layout.CYHorizontalLayout;
import com.hyena.coretext.layout.CYLayout;
import com.hyena.framework.utils.UIUtils;

import java.util.List;

/**
 * Created by yangzc on 17/3/3.
 */
public class CYSinglePageView extends CYPageView {

    private TextEnv mTextEnv;
    private String mQuestionTxt;
    private CYLayout mLayout;
    private List<ICYEditable> mEditableList;

    private List<CYBlock> blocks;

    public CYSinglePageView(Context context) {
        super(context);
        init();
    }

    public CYSinglePageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CYSinglePageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int width = getContext().getResources().getDisplayMetrics().widthPixels;
        mTextEnv = new TextEnv(getContext())
                .setPageWidth(width)
                .setTextColor(0xff333333)
                .setFontSize(UIUtils.dip2px(20))
                .setTextAlign(TextEnv.Align.CENTER)
                .setPageHeight(Integer.MAX_VALUE)
                .setVerticalSpacing(UIUtils.dip2px(getContext(), 3));
        mTextEnv.getEventDispatcher().addLayoutEventListener(this);
    }

    private void setText(String questionTxt) {
        if (TextUtils.isEmpty(questionTxt)) {
            this.mQuestionTxt = questionTxt;
            if (blocks != null && !blocks.isEmpty()) {
                for (int i = 0; i < blocks.size(); i++) {
                    blocks.get(i).release();
                }
            }
            blocks = null;
            return;
        }
        String text = questionTxt.replaceAll("\\\\#", "labelsharp")
                .replaceAll("\n", "").replaceAll("\r", "");
        this.mQuestionTxt = text;
        if (blocks != null && !blocks.isEmpty()) {
            for (int i = 0; i < blocks.size(); i++) {
                blocks.get(i).release();
            }
        }
        blocks = CYBlockProvider.getBlockProvider().build(mTextEnv, mQuestionTxt);
        mEditableList = getEditableList();
        doLayout(true);
    }

    public List<ICYEditable> getEditables() {
        return mEditableList;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTextEnv.setPageWidth(w);
        doLayout(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTextEnv != null && !mTextEnv.isEditable())
            return super.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void reLayout(boolean force) {
        if (blocks == null || blocks.isEmpty()) {
            setPageBlock(mTextEnv, null);
            return;
        }

        if (mLayout == null || force) {
            mLayout = new CYHorizontalLayout(mTextEnv, blocks);
        }
        if (mLayout != null) {
            List<CYPageBlock> pages = mLayout.parse();
            if (pages != null && pages.size() > 0) {
                CYPageBlock pageBlock = pages.get(0);
                pageBlock.setPadding(0, 0, 0, 0);
                setPageBlock(mTextEnv, pageBlock);
            }
        }
    }

    @Override
    public void doLayout(boolean force) {
        super.doLayout(force);
        reLayout(force);
        requestLayout();
        postInvalidate();
    }

    private Builder mBuilder = new Builder();

    public TextEnv getTextEnv() {
        return mTextEnv;
    }

    public Builder getBuilder() {
        return mBuilder;
    }

    public class Builder {

        private String mText;
        public Builder setEditable(boolean editable) {
            mTextEnv.setEditable(editable);
            return this;
        }

        public Builder setTextColor(int textColor) {
            mTextEnv.setTextColor(textColor);
            return this;
        }

        public Builder setTextSize(int dp) {
            mTextEnv.setFontSize(UIUtils.dip2px(getContext(), dp));
            return this;
        }

        public Builder setText(String questionTxt) {
            this.mText = questionTxt;
            return this;
        }

        public Builder setDebug(boolean debug) {
            mTextEnv.setDebug(debug);
            return this;
        }

        public void build() {
            CYSinglePageView.this.setText(mText);
        }
    }
}
