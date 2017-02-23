/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.blocks;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.hyena.coretext.CYPageView;
import com.hyena.coretext.TextEnv;
import com.hyena.framework.utils.UIUtils;

/**
 * Created by yangzc on 17/2/9.
 */
public class CYEditFace {

    private static final int ACTION_FLASH = 1;
    //刷新句柄
    private Handler mHandler;
    //是否显示输入提示（闪烁输入提示）
    private boolean mInputFlash = false;
    //是否可以编辑
    private boolean mIsEditable = true;

    private TextEnv mTextEnv;
    private ICYEditable mEditable;

    protected Paint mTextPaint;
    protected Paint mFlashPaint;
    protected Paint mBorderPaint;
    protected Paint mBackGroundPaint;
    protected Paint mDefaultTxtPaint;

    private String mDefaultText;
    private int paddingLeft, paddingTop, paddingRight, paddingBottom;
    private CYParagraphStyle mParagraphStyle;

    public CYEditFace(TextEnv textEnv, ICYEditable editable) {
        this.mTextEnv = textEnv;
        this.mEditable = editable;

        init();
    }

    protected void init() {
        //文本画笔
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.set(mTextEnv.getPaint());
        //默认文字画笔
        mDefaultTxtPaint = new Paint(mTextPaint);
        //闪动提示画笔
        mFlashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFlashPaint.setStrokeWidth(UIUtils.dip2px(mTextEnv.getContext(), 2));
        //边框画笔
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(Color.BLACK);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(2);
        //背景画笔
        mBackGroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackGroundPaint.setColor(Color.GRAY);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMessageImpl(msg);
            }
        };
    }

    public void setPadding(int left, int top, int right, int bottom) {
        this.paddingLeft = left;
        this.paddingTop = top;
        this.paddingRight = right;
        this.paddingBottom = bottom;
    }

    public void setDefaultText(String defaultText) {
        this.mDefaultText = defaultText;
    }

    public Paint getTextPaint() {
        return mTextPaint;
    }

    public Paint getDefaultTextPaint() {
        return mDefaultTxtPaint;
    }

    public Paint getBackGroundPaint() {
        return mBackGroundPaint;
    }

    public void onDraw(Canvas canvas, Rect blockRect, Rect contentRect) {
        drawBackGround(canvas, blockRect, contentRect);
        drawBorder(canvas, blockRect, contentRect);
        drawFlash(canvas, contentRect);
        String text = getText();
        if (TextUtils.isEmpty(text)) {
            drawDefaultText(canvas, contentRect);
        } else {
            drawText(canvas, getText(), contentRect);
        }
    }

    protected void drawBorder(Canvas canvas, Rect blockRect, Rect contentRect) {
        canvas.drawRect(blockRect, mBorderPaint);
    }

    protected void drawBackGround(Canvas canvas, Rect blockRect, Rect contentRect) {
        if (!hasFocus()) {
            canvas.drawRect(blockRect, mBackGroundPaint);
        }
    }

    protected void drawFlash(Canvas canvas, Rect contentRect) {
        if (mIsEditable && hasFocus() && mInputFlash) {
            String text = getText();
            float left;
            if (!TextUtils.isEmpty(text)) {
                float textWidth = mTextPaint.measureText(text);
                if (textWidth > contentRect.width()) {
                    left = contentRect.right;
                } else {
                    left = contentRect.left + (contentRect.width() + textWidth)/2;
                }
            } else {
                left = contentRect.left + contentRect.width()/2;
            }
            canvas.drawLine(left, contentRect.top, left, contentRect.bottom, mFlashPaint);
        }
    }

    protected void drawText(Canvas canvas, String text, Rect contentRect) {
        if (!TextUtils.isEmpty(text)) {
            float textWidth = mTextPaint.measureText(text);
            float contentWidth = contentRect.width();
            float x;
            if (textWidth > contentWidth) {
                x = contentRect.right - textWidth;
            } else {
                x = contentRect.left + (contentRect.width() - textWidth)/2;
            }
            canvas.save();
            canvas.clipRect(contentRect);
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();

            TextEnv.Align align = getTextEnv().getTextAlign();
            float y;
            if (align == TextEnv.Align.TOP) {
                y = contentRect.top + getTextHeight(mTextPaint) - fontMetrics.bottom;
            } else if(align == TextEnv.Align.CENTER) {
                y = contentRect.top + (contentRect.height() + getTextHeight(mTextPaint))/2 - fontMetrics.bottom;
            } else {
                y = contentRect.bottom - fontMetrics.bottom;
            }
            canvas.drawText(text, x, y, mTextPaint);
            canvas.restore();
        }
    }

    protected void drawDefaultText(Canvas canvas, Rect contentRect) {
        if (!TextUtils.isEmpty(mDefaultText)) {
            float textWidth = mDefaultTxtPaint.measureText(mDefaultText);
            float contentWidth = contentRect.width();
            float x;
            if (textWidth > contentWidth) {
                x = contentRect.right - textWidth;
            } else {
                x = contentRect.left + (contentRect.width() - textWidth)/2;
            }
            canvas.save();
            canvas.clipRect(contentRect);
            Paint.FontMetrics fontMetrics = mDefaultTxtPaint.getFontMetrics();
            canvas.drawText(mDefaultText, x, contentRect.bottom - fontMetrics.bottom, mTextPaint);
            canvas.restore();
        }
    }

    private void handleMessageImpl(Message msg) {
        int what = msg.what;
        switch (what) {
            case ACTION_FLASH: {
                mInputFlash = !mInputFlash;
                Message next = mHandler.obtainMessage(ACTION_FLASH);
                mHandler.sendMessageDelayed(next, 500);
                if (mTextEnv != null)
                    mTextEnv.getEventDispatcher().postInvalidate(null);
                break;
            }
            default:
                break;
        }
    }

    public String getText() {
        if (mTextEnv == null || mEditable == null)
            return null;
        return mTextEnv.getEditableValue(mEditable.getTabId());
    }

    public void setText(String text) {
        if (mTextEnv == null || mEditable == null)
            return;
        mTextEnv.setEditableValue(mEditable.getTabId(), text);
        if (mTextEnv != null) {
//            mTextEnv.getEventDispatcher().postInvalidate();
            mTextEnv.getEventDispatcher().requestLayout();
        }
    }

    public void setEditable(boolean isEditable) {
        this.mIsEditable = isEditable;
        if (mTextEnv != null)
            mTextEnv.getEventDispatcher().postInvalidate(null);
    }

    public void setFocus(boolean hasFocus) {
        if (hasFocus && mEditable != null) {
            CYPageView.FOCUS_TAB_ID = mEditable.getTabId();
        }
        if (hasFocus()) {
            mHandler.removeMessages(ACTION_FLASH);
            Message next = mHandler.obtainMessage(ACTION_FLASH);
            mHandler.sendMessageDelayed(next, 500);
        } else {
            mHandler.removeMessages(ACTION_FLASH);
        }
        if (mTextEnv != null)
            mTextEnv.getEventDispatcher().postInvalidate(null);
    }

    /**
     * 是否可在编辑模式
     * @return
     */
    public boolean hasFocus() {
        return mIsEditable && CYPageView.FOCUS_TAB_ID == mEditable.getTabId();
    }

    public TextEnv getTextEnv() {
        return mTextEnv;
    }

    public void setParagraphStyle(CYParagraphStyle style) {
        this.mParagraphStyle = style;
    }

    public void release() {
        mHandler.removeMessages(ACTION_FLASH);
    }

    public int getTextHeight(Paint paint) {
        return (int) (Math.ceil(paint.descent() - paint.ascent()) + 0.5f);
    }
}
