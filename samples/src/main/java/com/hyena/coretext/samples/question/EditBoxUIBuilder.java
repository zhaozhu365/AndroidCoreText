/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples.question;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditable;
import com.hyena.coretext.event.CYEventDispatcher;
import com.hyena.framework.utils.UIUtils;

/**
 * Created by yangzc on 17/2/9.
 */
public class EditBoxUIBuilder {

    private static final int ACTION_FLASH = 1;
    //刷新句柄
    private Handler mHandler;
    //是否显示输入提示（闪烁输入提示）
    private boolean mInputFlash = false;
    //是否可以编辑
    private boolean mIsEditable = true;
    //是否是输入状态
    private boolean mIsInEditMode = true;

    private TextEnv mTextEnv;
    private CYEditable mEditable;
    private Rect mBlockRect;
    private Rect mContentRect;

    protected Paint mTextPaint;
    protected Paint mFlashPaint;

    public EditBoxUIBuilder(TextEnv textEnv, Rect blockRect, Rect contentRect, CYEditable editable) {
        this.mTextEnv = textEnv;
        this.mBlockRect = blockRect;
        this.mContentRect = contentRect;
        this.mEditable = editable;

        init();
    }
    private void init() {
        //文本画笔
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.set(mTextEnv.getPaint());
        //闪动提示画笔
        mFlashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFlashPaint.setStrokeWidth(UIUtils.dip2px(mTextEnv.getContext(), 2));

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMessageImpl(msg);
            }
        };
    }

    protected void onDraw(Canvas canvas) {
        drawBorder(canvas);
        drawBackGround(canvas);
        drawFlash(canvas);
        drawText(canvas);
    }

    protected void drawBorder(Canvas canvas) {
    }

    protected void drawBackGround(Canvas canvas) {
    }

    protected void drawFlash(Canvas canvas) {
        if (mIsEditable && mIsInEditMode) {
            String text = getText();
            float left = mContentRect.left;
            if (!TextUtils.isEmpty(text)) {
                left += mTextPaint.measureText(text);
            }

        }
    }

    protected void drawText(Canvas canvas) {
        String text = getText();
        if (!TextUtils.isEmpty(text)) {
            float textWidth = mTextPaint.measureText(text);
            float contentWidth = mContentRect.width();

            int x = 0;
            int y = 0;
            canvas.save();
            canvas.clipRect(mContentRect);
            canvas.drawText(text, x, y, mTextPaint);
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

    protected String getText() {
        return "";
    }

    public void release() {
        mHandler.removeMessages(ACTION_FLASH);
    }

}
