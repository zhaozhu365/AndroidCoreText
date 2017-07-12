/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.builder.CYBlockProvider;
import com.hyena.coretext.layout.CYHorizontalLayout;
import com.hyena.coretext.utils.Const;
import com.hyena.framework.utils.UiThreadHandler;

import java.util.List;

/**
 * Created by yangzc on 17/3/3.
 */
public class CYSinglePageView extends CYPageView {

    private String mQuestionTxt;
    private List<ICYEditable> mEditableList;

    private List<CYBlock> blocks;

    public CYSinglePageView(Context context) {
        super(context);
    }

    public CYSinglePageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CYSinglePageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void setText(String questionTxt) {
        this.mQuestionTxt = questionTxt;
    }

    private void build() {
        if (TextUtils.isEmpty(mQuestionTxt)) {
            if (blocks != null && !blocks.isEmpty()) {
                for (int i = 0; i < blocks.size(); i++) {
                    blocks.get(i).release();
                }
            }
            blocks = null;
            return;
        }
        String text = mQuestionTxt.replaceAll("\\\\#", "labelsharp")
                .replaceAll("\n", "").replaceAll("\r", "");
        if (blocks != null && !blocks.isEmpty()) {
            for (int i = 0; i < blocks.size(); i++) {
                blocks.get(i).release();
            }
        }
        blocks = CYBlockProvider.getBlockProvider().build(getTextEnv(), text);
        mEditableList = findEditableList();
        doLayout(true);
    }

    public List<ICYEditable> getEditableList() {
        return mEditableList;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        if (width > 0) {
            if (getPageBlock() != null && getTextEnv().getPageWidth() == width) {
                setMeasuredDimension(width, getPageBlock().getHeight());
            } else {
                getTextEnv().setPageWidth(width);
                //measure
                CYPageBlock pageBlock = parsePageBlock();
                setPageBlock(pageBlock);
                setMeasuredDimension(width, pageBlock == null ? 0 : pageBlock.getHeight());
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void doLayout(boolean force) {
        super.doLayout(force);
        if (force) {
            CYPageBlock pageBlock = parsePageBlock();
            setPageBlock(pageBlock);
        }
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
                postInvalidate();
            }
        });
    }

    private CYPageBlock parsePageBlock() {
        if (blocks != null && !blocks.isEmpty()) {
            CYHorizontalLayout layout = new CYHorizontalLayout(getTextEnv(), blocks);
            List<CYPageBlock> pages = layout.parse();
            if (pages != null && pages.size() > 0) {
                CYPageBlock pageBlock = pages.get(0);
                pageBlock.setPadding(0, 0, 0, 0);
                return pageBlock;
            }
        }
        return null;
    }

    private Builder mBuilder = new Builder();

    public Builder getBuilder() {
        return mBuilder;
    }

    public class Builder {

        private String mText;
        public Builder setEditable(boolean editable) {
            getTextEnv().setEditable(editable);
            return this;
        }

        public Builder setTextColor(int textColor) {
            getTextEnv().setTextColor(textColor);
            return this;
        }

        public Builder setTextSize(int dp) {
            getTextEnv().setFontSize(Const.DP_1 * dp);
            return this;
        }

        public Builder setText(String questionTxt) {
            CYSinglePageView.this.setText(questionTxt);
            return this;
        }

        public Builder setDebug(boolean debug) {
            getTextEnv().setDebug(debug);
            return this;
        }

        public void build() {
            CYSinglePageView.this.build();
        }
    }
}
