package com.hyena.coretext.blocks;

import android.graphics.Canvas;

import com.hyena.coretext.TextEnv;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.UIUtils;

import java.util.List;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYLineBlock extends CYBlock<CYBlock> {

    private int mWidth, mHeight;
    private boolean isInMonopolyRow;
    private CYParagraphStyle mParagraphStyle;
    private int mMaxHeightInLine = 0;
    private boolean isValid = false;

    private static final int DP_20 = UIUtils.dip2px(20);

    public CYLineBlock(TextEnv textEnv, CYParagraphStyle style) {
        super(textEnv, "");
        this.mParagraphStyle = style;
    }

    @Override
    public int getContentWidth() {
        return mWidth;
    }

    @Override
    public int getContentHeight() {
        if (mHeight <= 0)
            mHeight = DP_20;
        return mHeight;
    }

    @Override
    public int getLineHeight() {
        return getContentHeight();
    }

    @Override
    public void draw(Canvas canvas) {
        List<CYBlock> children = getChildren();
        if (children != null) {
            int count = children.size();
            for (int i = 0; i < count; i++) {
                CYBlock block = children.get(i);
                block.draw(canvas);
            }
        }
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public void addChild(CYBlock child) {
        super.addChild(child);
        if (!child.isValid())
            return;

        int width = child.getWidth();
        int height = child.getHeight();

        /*
            如果某一行中存在环绕效果和非环绕效果并存的情况,则该行忽略环绕效果，但不影响下一行展现
         */
        boolean isInMonoMode = false;
        if (child instanceof CYPlaceHolderBlock
                && ((((CYPlaceHolderBlock)child).getAlignStyle() == CYPlaceHolderBlock.AlignStyle.Style_Normal)
                || ((CYPlaceHolderBlock)child).getAlignStyle() == CYPlaceHolderBlock.AlignStyle.Style_MONOPOLY)) {
            isInMonopolyRow = true;
            isInMonoMode = true;
        }

        //更新行高
        if (child instanceof CYTextBlock || isInMonoMode) {
            if (height > mHeight) {
                this.mHeight = height;
            }
        }

        //更新行中最大高度
        if (height > mMaxHeightInLine) {
            mMaxHeightInLine = height;
        }

        this.mWidth += width;
        this.isValid = true;
    }

    @Override
    public void onMeasure() {
        List<CYBlock> blocks = getChildren();
        if (blocks != null && !blocks.isEmpty()) {
            int count = blocks.size();
            for (int i = 0; i < count; i++) {
                CYBlock block = blocks.get(i);
                block.onMeasure();
            }
        }
        super.onMeasure();
    }

    public int getMaxBlockHeightInLine() {
        return mMaxHeightInLine;
    }

    public void updateLineY(int lineY) {
        setLineY(lineY);
        List<CYBlock> children = getChildren();
        if (children != null) {
            int appendX = 0;
            if (mParagraphStyle != null) {
                if(mParagraphStyle.getHorizontalAlign() == CYHorizontalAlign.CENTER) {
                    appendX = (getTextEnv().getPageWidth() - getWidth()) >> 1;
                } else if (mParagraphStyle.getHorizontalAlign() == CYHorizontalAlign.RIGHT){
                    appendX = getTextEnv().getPageWidth() - getWidth();
                }
            }
            int lineHeight = getLineHeight();
            int childCount = children.size();
            for (int i = 0; i < childCount; i++) {
                CYBlock child = children.get(i);
                child.setIsInMonopolyRow(isInMonopolyRow);
                child.setX(child.getX() + appendX);
                child.setLineY(lineY);
                child.setLineHeight(lineHeight);
            }
        }
    }

    public void setIsFinishingLineInParagraph(boolean isFinishingLine) {
        if (isFinishingLine && mParagraphStyle != null) {
            setPadding(0, getPaddingTop(), 0, mParagraphStyle.getMarginBottom());
        }
    }

    public void setIsFirstLineInParagraph(boolean isFirstLine) {
        if (isFirstLine && mParagraphStyle != null) {
            setPadding(0, mParagraphStyle.getMarginTop(), 0, getPaddingBottom());
        }
    }
}
