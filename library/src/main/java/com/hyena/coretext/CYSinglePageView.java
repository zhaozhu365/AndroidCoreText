/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext;

import android.content.Context;
import android.util.AttributeSet;

import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.builder.CYBlockProvider;
import com.hyena.coretext.layout.CYHorizontalLayout;
import com.hyena.coretext.utils.CachedPage;
import com.hyena.coretext.utils.Const;
import com.hyena.framework.utils.UiThreadHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    @Override
    public ICYEditable findEditableByTabId(int tabId) {
        List<ICYEditable> editableList = getEditableList();
        for (int i = 0; i < editableList.size(); i++) {
            ICYEditable editable = editableList.get(i);
            if (editable.getTabId() == tabId)
                return editable;
        }
        return null;
    }

    private void setText(String questionTxt) {
        this.mQuestionTxt = questionTxt;
    }

    private void build() {
        if (mQuestionTxt == null)
            mQuestionTxt = "";

        String text = mQuestionTxt/*.replaceAll("\\\\#", "labelsharp")*/
                .replaceAll("\n", "").replaceAll("\r", "");
        if (getPageBlock() != null) {
            getPageBlock().stop();
        }
        CachedPage cachedPage = getTextEnv().getCachedPage(mQuestionTxt);
        if (cachedPage != null && cachedPage.mPageBlock != null) {
            cachedPage.mPageBlock.restart();
            this.blocks = cachedPage.mBlocks;
            setPageBlock(cachedPage.mPageBlock);
            mEditableList = findEditableList();
            requestLayout();
            postInvalidate();
        } else {
            blocks = CYBlockProvider.getBlockProvider().build(getTextEnv(), text);
            doLayout(true);
            mEditableList = findEditableList();
        }
    }

    public List<ICYEditable> getEditableList() {
        return mEditableList;
    }

    public List<ICYEditable> findEditableList() {
        List<ICYEditable> editableList = new ArrayList<>();
        if (blocks != null && !blocks.isEmpty()) {
            for (int i = 0; i < blocks.size(); i++) {
                blocks.get(i).findAllEditable(editableList);
            }
        }
        Collections.sort(editableList, new Comparator<ICYEditable>() {
            @Override
            public int compare(ICYEditable lhs, ICYEditable rhs) {
                return lhs.getTabId() - rhs.getTabId();
            }
        });
        return editableList;
    }

    @Override
    public void setPageBlock(CYPageBlock pageBlock) {
        //cache page
        super.setPageBlock(pageBlock);
        getTextEnv().setCachePage(mQuestionTxt, pageBlock, blocks);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CYPageBlock pageBlock = getPageBlock();
        if (pageBlock != null && pageBlock.getMeasureWidth()
                == getTextEnv().getSuggestedPageWidth()) {
            setMeasuredDimension(getSize(pageBlock.getWidth(), widthMeasureSpec),
                    getSize(pageBlock.getHeight(), heightMeasureSpec));
        } else {
            int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            getTextEnv().setSuggestedPageWidth(width);
            pageBlock = parsePageBlock();
            setPageBlock(pageBlock);
            if (pageBlock != null) {
                setMeasuredDimension(getSize(pageBlock.getWidth(), widthMeasureSpec),
                        getSize(pageBlock.getHeight(), heightMeasureSpec));
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    private int getSize(int defaultSize, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                return defaultSize;
            case MeasureSpec.EXACTLY:
                return specSize;
        }
        return defaultSize;
    }

    @Override
    public void doLayout(boolean force) {
        super.doLayout(force);
        //宽度不合法，则抛弃
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
