package com.hyena.coretext.blocks;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.hyena.coretext.TextEnv;

import java.util.List;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYTextBlock extends CYBlock {

    private String text;
    private Paint mPaint;

    public CYTextBlock(TextEnv textEnv, String content){
        super(textEnv, content);
        this.text = content;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.set(textEnv.getPaint());
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    public CYTextBlock(TextEnv textEnv, Paint paint, String content) {
        super(textEnv, content);
        this.mPaint = paint;
        if (mPaint == null)
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        this.text = content;
    }

    public CYTextBlock setTextColor(int color) {
        if (mPaint != null && color > 0) {
            mPaint.setColor(color);
        }
        return this;
    }

    public CYTextBlock setTypeFace(Typeface typeface){
        if (mPaint != null && typeface != null) {
            mPaint.setTypeface(typeface);
        }
        return this;
    }

    public CYTextBlock setTextSize(int fontSize){
        if (mPaint != null && fontSize > 0) {
            mPaint.setTextSize(fontSize);
        }
        return this;
    }

    @Override
    public List<CYBlock> getChildren() {
        if (!TextUtils.isEmpty(text)) {
            if (text.length() == 1) {
                return null;
            } else {
                List<CYBlock> children = super.getChildren();
                if (children == null || children.isEmpty()) {
                    parseSubBlocks();
                }
                return children;
            }
        }
        return null;
    }

    private void parseSubBlocks() {
        if (!TextUtils.isEmpty(text)) {
            for (int i = 0; i < text.length(); i++) {
                String word = text.substring(i, i + 1);
                CYTextBlock block = new CYTextBlock(getTextEnv(), mPaint, word);
                addChild(block);
            }
        }
    }

    @Override
    public int getContentWidth() {
        return (int) mPaint.measureText(text);
    }

    @Override
    public int getContentHeight() {
        return (int) (Math.ceil(mPaint.descent() - mPaint.ascent()) + 0.5f);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect rect = getContentRect();
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        canvas.drawText(text, rect.centerX(), rect.bottom - fontMetrics.bottom, mPaint);
    }
}
