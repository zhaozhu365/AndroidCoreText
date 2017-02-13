/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.blocks;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.hyena.coretext.CYPageView;
import com.hyena.coretext.TextEnv;
import com.hyena.coretext.event.CYEventDispatcher;
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

    private Rect mContentRect = new Rect();
    private int paddingLeft, paddingTop, paddingRight, paddingBottom;

    public CYEditFace(TextEnv textEnv, ICYEditable editable) {
        this.mTextEnv = textEnv;
        this.mEditable = editable;

        init();
    }

    protected void init() {
        //文本画笔
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.set(mTextEnv.getPaint());
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

    public Paint getTextPaint() {
        return mTextPaint;
    }

    public Paint getBackGroundPaint() {
        return mBackGroundPaint;
    }

    public void onDraw(Canvas canvas, Rect blockRect) {
        mContentRect.set(blockRect.left + paddingLeft, blockRect.top + paddingTop,
                blockRect.right - paddingRight, blockRect.bottom - paddingBottom);

        drawBorder(canvas, blockRect);
        drawBackGround(canvas, blockRect);
        drawFlash(canvas, mContentRect);
        drawText(canvas, mContentRect);
    }

    protected void drawBorder(Canvas canvas, Rect blockRect) {
        canvas.drawRect(blockRect, mBorderPaint);
    }

    protected void drawBackGround(Canvas canvas, Rect blockRect) {
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

    protected void drawText(Canvas canvas, Rect contentRect) {
        String text = getText();
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
            canvas.drawText(text, x, contentRect.bottom - fontMetrics.bottom, mTextPaint);
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
                CYEventDispatcher.getEventDispatcher().postInvalidate();
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
        CYEventDispatcher.getEventDispatcher().postInvalidate();
    }

    public void setEditable(boolean isEditable) {
        this.mIsEditable = isEditable;
        CYEventDispatcher.getEventDispatcher().postInvalidate();
    }

    public void setFocus(boolean hasFocus) {
        if (hasFocus()) {
            mHandler.removeMessages(ACTION_FLASH);
            Message next = mHandler.obtainMessage(ACTION_FLASH);
            mHandler.sendMessageDelayed(next, 500);
        } else {
            mHandler.removeMessages(ACTION_FLASH);
        }
        CYEventDispatcher.getEventDispatcher().postInvalidate();
    }

    /**
     * 是否可在编辑模式
     * @return
     */
    public boolean hasFocus() {
        return mIsEditable && CYPageView.FOCUS_TAB_ID == mEditable.getTabId();
    }

    public void release() {
        mHandler.removeMessages(ACTION_FLASH);
    }

}
