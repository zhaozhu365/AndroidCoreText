package com.hyena.coretext.blocks;

import android.graphics.Canvas;

import com.hyena.coretext.TextEnv;
import com.hyena.framework.utils.UIUtils;

import java.util.List;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYLineBlock extends CYBlock<CYBlock> {

    private int mLineHeight;
    private CYParagraphStyle mParagraphStyle;
    public CYLineBlock(TextEnv textEnv, CYParagraphStyle style) {
        super(textEnv, "");
        this.mParagraphStyle = style;
    }

    @Override
    public int getContentWidth() {
        List<CYBlock> blocks = getChildren();
        int width = 0;
        if (blocks != null && !blocks.isEmpty()) {
            for (int i = 0; i < blocks.size(); i++) {
                width += blocks.get(i).getWidth();
            }
        }
        return width;
    }

    @Override
    public int getContentHeight() {
        return getLineHeight();
    }

    @Override
    public void draw(Canvas canvas) {
        if (getChildren() != null) {
            if (mParagraphStyle != null) {
                canvas.save();
                if (mParagraphStyle.getHorizontalAlign() == CYHorizontalAlign.LEFT) {
                    canvas.translate(0, 0);
                } else if(mParagraphStyle.getHorizontalAlign() == CYHorizontalAlign.CENTER) {
                    canvas.translate((getTextEnv().getPageWidth() - getWidth())/2, 0);
                } else {
                    canvas.translate(getTextEnv().getPageWidth() - getWidth(), 0);
                }
            }
            for (int i = 0; i < getChildren().size(); i++) {
                getChildren().get(i).draw(canvas);
            }
            if (mParagraphStyle != null) {
                canvas.restore();
            }
        }
    }

    @Override
    public int getLineHeight() {
        if (mLineHeight <= 0) {
            measure();
        }
        if (mLineHeight <= 0) {
            mLineHeight = UIUtils.dip2px(20);
        }
        return mLineHeight;
    }

    public void measure() {
        measureLineHeight();
        syncBlocksHeight();
    }

    public void updateLineY(int lineY) {
        if (getChildren() != null) {
            boolean isInMonopolyRow = false;
            for (int i = 0; i < getChildren().size(); i++) {
                CYBlock child = getChildren().get(i);
                if (child instanceof CYPlaceHolderBlock
                        && ((((CYPlaceHolderBlock)child).getAlignStyle() == CYPlaceHolderBlock.AlignStyle.Style_Normal)
                        || ((CYPlaceHolderBlock)child).getAlignStyle() == CYPlaceHolderBlock.AlignStyle.Style_MONOPOLY)) {
                    isInMonopolyRow = true;
                }
            }
            for (int i = 0; i < getChildren().size(); i++) {
                getChildren().get(i).setLineY(lineY);
                getChildren().get(i).setIsInMonopolyRow(isInMonopolyRow);
            }
        }
    }

    @Override
    public void addChild(CYBlock child) {
        super.addChild(child);
        if (child != null) {
            child.setParagraphStyle(mParagraphStyle);
        }
    }

    private void measureLineHeight(){
        if (getChildren() != null) {
            mLineHeight = 0;
            for (int i = 0; i < getChildren().size(); i++) {
                CYBlock block = getChildren().get(i);
                if (block instanceof CYTextBlock || (block instanceof CYPlaceHolderBlock
                        && (((CYPlaceHolderBlock)block).getAlignStyle() == CYPlaceHolderBlock.AlignStyle.Style_Normal)
                        || ((CYPlaceHolderBlock)block).getAlignStyle() == CYPlaceHolderBlock.AlignStyle.Style_MONOPOLY)) {
                    if (block.getHeight() > mLineHeight) {
                        mLineHeight = block.getHeight();
                    }
                }
            }
            if (mLineHeight <= 0) {
                mLineHeight = getMaxBlockHeightInLine();
            }
        }
    }

    public int getMaxBlockHeightInLine(){
        int maxHeight = 0;
        if (getChildren() != null) {
            for (int i = 0; i < getChildren().size(); i++) {
                CYBlock block = getChildren().get(i);
                if (block.getHeight() > maxHeight) {
                    maxHeight = block.getHeight();
                }
            }
        }
        return maxHeight;
    }

    private void syncBlocksHeight(){
        if (getChildren() != null) {
            for (int i = 0; i < getChildren().size(); i++) {
                CYBlock block = getChildren().get(i);
                block.setLineHeight(mLineHeight);
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
