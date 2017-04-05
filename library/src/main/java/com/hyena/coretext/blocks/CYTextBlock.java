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

    private int start;
    private int end;
    private String text;

    private Paint mPaint;
    private int mWidth, mHeight;
    private boolean mIsWord = false;
    Paint.FontMetrics mFontMetrics;

    public CYTextBlock(TextEnv textEnv, String content){
        super(textEnv, content);
        this.text = content;
        this.start = 0;
        this.end = content.length() - 1;

        mPaint = new Paint();
        mPaint.set(textEnv.getPaint());
        mPaint.setAntiAlias(false);
        mIsWord = false;
        parseSubBlocks();
    }

    private CYTextBlock buildChildBlock(TextEnv textEnv, Paint paint
            , int width, int height, String content, int start, int end) {
        try {
            CYTextBlock textBlock = (CYTextBlock) clone();
            textBlock.setTextEnv(textEnv);
            textBlock.mPaint = paint;
            textBlock.mWidth = width;
            textBlock.mHeight = height;
            textBlock.text = content;
            textBlock.mIsWord = true;
            textBlock.start= start;
            textBlock.end = end;
            return textBlock;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setParagraphStyle(CYParagraphStyle style) {
        super.setParagraphStyle(style);
        if (style != null) {
            mPaint.setTextSize(style.getTextSize());
            mPaint.setColor(style.getTextColor());
        }
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
        if (mIsWord) {
            return null;
        }
        return super.getChildren();
    }

    private void parseSubBlocks() {
        if (!TextUtils.isEmpty(text)) {
            char ch[] = text.toCharArray();
            int blockHeight = getTextHeight(mPaint);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            TextEnv textEnv = getTextEnv();
            for (int i = 0; i < ch.length; i++) {
                int wordStart = i;
                while (isLetter(ch[i]) && !Character.isSpace(ch[i])) {
                    i ++;
                }
                int blockWidth = (int) mPaint.measureText(text, wordStart, i + 1);
                CYTextBlock block = buildChildBlock(textEnv, mPaint,
                        blockWidth, blockHeight, text, wordStart, i + 1);
                if (block != null) {
                    block.mFontMetrics = fontMetrics;
                    addChild(block);
                }
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
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mFontMetrics != null && mIsWord) {
            Rect rect = getContentRect();
            float x = rect.left;
            float y = rect.bottom - mFontMetrics.bottom;
            canvas.drawText(text, start, end, x, y, mPaint);
        }
    }

    public static boolean isLetter(char ch) {
        if (('A' <= ch && ch <= 'Z') || ('a' <= ch && ch <= 'z') || ch == '-') {
            return true;
        }
        return false;
    }
}
