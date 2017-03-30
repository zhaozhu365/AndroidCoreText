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
    private int mWidth, mHeight;
    private boolean mIsWord = false;
    Paint.FontMetrics mFontMetrics;

    public CYTextBlock(TextEnv textEnv, String content){
        super(textEnv, content);
        this.text = content;
        mPaint = new Paint();
        mPaint.set(textEnv.getPaint());
        mPaint.setAntiAlias(false);
//        mPaint.setTextAlign(Paint.Align.CENTER);
        mIsWord = false;
        parseSubBlocks();
    }

    private CYTextBlock buildChildBlock(TextEnv textEnv, Paint paint
            , int width, int height, String content) {
        try {
            CYTextBlock textBlock = (CYTextBlock) clone();
            textBlock.setTextEnv(textEnv);
            textBlock.mPaint = paint;
            textBlock.mWidth = width;
            textBlock.mHeight = height;
            textBlock.text = content;
            textBlock.mIsWord = true;
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
                int wordStart = i, count = 1;
                while ((i + 1) < ch.length && isLetter(ch[i + 1])
                        && !Character.isSpace(ch[i + 1])) {
                    count ++;
                    i ++;
                }
                String word = new String(ch, wordStart, count);
                int blockWidth = (int) mPaint.measureText(word);
                CYTextBlock block = buildChildBlock(textEnv, mPaint,
                        blockWidth, blockHeight, word);
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
        if (mFontMetrics != null) {
            Rect rect = getContentRect();
            float x = rect.left;
            float y = rect.bottom - mFontMetrics.bottom;
            canvas.drawText(text, x, y, mPaint);
        }
    }

    public static boolean isLetter(char ch) {
        if (('A' <= ch && ch <= 'Z') || ('a' <= ch && ch <= 'z') || ch == '-') {
            return true;
        }
        return false;
    }
}
