package com.hyena.coretext.blocks;

import android.graphics.Canvas;

import com.hyena.coretext.TextEnv;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYPageBlock extends CYBlock<CYLineBlock> {

    private int mWidth, mHeight;
    private int mMeasureWidth;
    public CYPageBlock(TextEnv textEnv) {
        super(textEnv, "");
        this.mMeasureWidth = textEnv.getSuggestedPageWidth();
    }

    public int getMeasureWidth() {
        return mMeasureWidth;
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
    public void addChild(CYLineBlock child) {
        super.addChild(child);
        child.setParent(this);
        int width = child.getWidth() + child.getMarginLeft() + child.getMarginRight();
        int height = child.getHeight();
        int lineY = child.getLineY();
        if (width > mWidth) {
            mWidth = width;
        }
        if (lineY + height > mHeight) {
            mHeight = lineY + height;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        List<CYLineBlock> children = getChildren();
        if (children != null) {
            int count = children.size();
            for (int i = 0; i < count; i++) {
                CYLineBlock lineBlock = children.get(i);
                lineBlock.draw(canvas);
            }
        }
        canvas.restore();
    }

    public List<CYBlock> getBlocks() {
        List<CYBlock> blocks = new ArrayList<CYBlock>();
        List<CYLineBlock> lines = getChildren();
        if (lines != null) {
            int lineCount = lines.size();
            for (int i = 0; i < lineCount; i++) {
                CYLineBlock line = lines.get(i);
                blocks.addAll(line.getChildren());
            }
        }
        return blocks;
    }

    @Override
    public void onMeasure() {
        List<CYLineBlock> lines = getChildren();
        if (lines != null) {
            int lineCount = lines.size();
            for (int i = 0; i < lineCount; i++) {
                CYLineBlock line = lines.get(i);
                line.onMeasure();
            }
        }
        super.onMeasure();
    }
}
