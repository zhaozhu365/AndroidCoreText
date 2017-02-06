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
//    private int color;
//    private int fontSize;
//    private Typeface typeface;

    private Paint mPaint;

    public CYTextBlock(TextEnv textEnv, String content){
        super(textEnv, content);
        this.text = content;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.set(textEnv.getPaint());
    }

    public CYTextBlock(TextEnv textEnv, Paint paint, String content) {
        super(textEnv, content);
        this.mPaint = paint;
        if (mPaint == null)
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        this.text = content;
    }

    public CYTextBlock setTextColor(int color) {
//        this.color = color;
//        createOrClonePaint();
        if (mPaint != null && color > 0) {
            mPaint.setColor(color);
        }
        return this;
    }

    public CYTextBlock setTypeFace(Typeface typeface){
//        this.typeface = typeface;
//        createOrClonePaint();
        if (mPaint != null && typeface != null) {
            mPaint.setTypeface(typeface);
        }
        return this;
    }

    public CYTextBlock setTextSize(int fontSize){
//        this.fontSize = fontSize;
//        createOrClonePaint();
        if (mPaint != null && fontSize > 0) {
            mPaint.setTextSize(fontSize);
        }
        return this;
    }

//    private void createOrClonePaint() {
//        if (mPaint == null) {
//            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        }
//        mPaint.set(getTextEnv().getPaint());
//    }

//    private Paint getPaint() {
//        if (mPaint != null) {
//            return mPaint;
//        }
//        return getTextEnv().getPaint();
//    }

    @Override
    public List<CYBlock> getChildren() {
        List<CYBlock> children = super.getChildren();
        if (children == null || children.isEmpty()) {
            parseSubBlocks();
        }
        return children;
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
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        return (int) (Math.ceil(fm.bottom - fm.top) + 0.5f);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect rect = getContentRect();
        canvas.drawText(text, rect.left , rect.top + (getLineHeight() + getContentHeight())/2, mPaint);
    }
}
