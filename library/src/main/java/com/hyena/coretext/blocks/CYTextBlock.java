package com.hyena.coretext.blocks;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.hyena.coretext.TextEnv;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYTextBlock extends CYBlock {

    private int start;
    private int count;
    private char chs[];

    private Paint mPaint;
    private int mWidth;
    private Value<Integer> mHeight = new Value<Integer>(0);
    private boolean mIsWord = false;
    Paint.FontMetrics mFontMetrics;

    public CYTextBlock(TextEnv textEnv, String content){
        super(textEnv, content);
        if (TextUtils.isEmpty(content))
            content = "";
        this.chs = content.toCharArray();
        this.start = 0;
        this.count = chs.length;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.set(textEnv.getPaint());
        mIsWord = false;
        parseSubBlocks();
    }

    private CYTextBlock buildChildBlock(TextEnv textEnv, Paint paint
            , int width, Value<Integer> height, char[] chs, int start, int count) {
        try {
            CYTextBlock textBlock = (CYTextBlock) clone();
            textBlock.setTextEnv(textEnv);
            textBlock.mPaint = paint;
            textBlock.mWidth = width;
            textBlock.mHeight = height;
            textBlock.chs = chs;
            textBlock.mIsWord = true;
            textBlock.start= start;
            textBlock.count = count;
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
            mPaint.setColor(style.getTextColor());
            mPaint.setTextSize(style.getTextSize());
            this.mWidth = (int) mPaint.measureText(chs, start, count);
            this.mHeight.setValue(getTextHeight(mPaint));
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
            this.mWidth = (int) mPaint.measureText(chs, start, count);
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
        if (chs.length > 0) {
            setChildren(new ArrayList(chs.length));
            Value blockHeight = new Value<Integer>(getTextHeight(mPaint));
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            TextEnv textEnv = getTextEnv();
            for (int i = 0; i < chs.length; i++) {
                int wordStart = i, count = 1;
                while ((i + 1) < chs.length && isLetter(chs[i + 1])
                        && !Character.isSpace(chs[i + 1])) {
                    count ++;
                    i ++;
                }
                int blockWidth = (int) mPaint.measureText(chs, wordStart, count);
                CYTextBlock block = buildChildBlock(textEnv, mPaint,
                        blockWidth, blockHeight, chs, wordStart, count);
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
        return mHeight.getValue().intValue();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mFontMetrics != null && mIsWord) {
            Rect rect = getContentRect();
            float x = rect.left;
            float y = rect.bottom - mFontMetrics.bottom;
            canvas.drawText(chs, start, count, x, y, mPaint);

            CYParagraphStyle paragraphStyle = getParagraphStyle();
            if (paragraphStyle != null) {
                String style = paragraphStyle.getStyle();
                if ("under_line".equals(style)) {
                    canvas.drawLine(x, rect.bottom, x + rect.width(), rect.bottom, mPaint);
                }
            }
        }
    }

    public static boolean isLetter(char ch) {
        if (('A' <= ch && ch <= 'Z') || ('a' <= ch && ch <= 'z') || ch == '-') {
            return true;
        }
        return false;
    }

    class Value<T> {
        private T mValue;
        public Value(T value) {
            this.mValue = value;
        }
        public void setValue(T value) {
            this.mValue = value;
        }
        public T getValue() {
            return mValue;
        }
    }
}
