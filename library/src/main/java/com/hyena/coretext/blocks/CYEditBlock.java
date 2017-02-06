package com.hyena.coretext.blocks;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.hyena.coretext.TextEnv;

/**
 * Created by yangzc on 16/4/12.
 */
public class CYEditBlock extends CYPlaceHolderBlock {

    private static final int ACTION_FLASH = 1;

    private Paint mBgPaint;
    private Paint mBorderPaint;
    private Paint mInputHintPaint;

    private boolean mInputHintVisible = false;
    private Handler mHandler;

    public CYEditBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init();
    }

    private void init(){
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(Color.GRAY);
        mBgPaint.setStrokeWidth(1);

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(Color.BLACK);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(2);

        mInputHintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInputHintPaint.setColor(Color.RED);
        mInputHintPaint.setStrokeWidth(2);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMessageImpl(msg);
            }
        };
    }

    private void handleMessageImpl(Message msg) {
        int what = msg.what;
        switch (what) {
            case ACTION_FLASH: {
                mInputHintVisible = !mInputHintVisible;

                postInvalidate();

                Message next = mHandler.obtainMessage(ACTION_FLASH);
                mHandler.sendMessageDelayed(next, 500);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public CYEditBlock setWidth(int width) {
        return (CYEditBlock) super.setWidth(width);
    }

    @Override
    public CYEditBlock setHeight(int height) {
        return (CYEditBlock) super.setHeight(height);
    }

    @Override
    public CYEditBlock setAlignStyle(AlignStyle style) {
        return (CYEditBlock) super.setAlignStyle(style);
    }

    @Override
    public void setFocus(boolean focus) {
        super.setFocus(focus);
        if (focus) {
            mHandler.removeMessages(ACTION_FLASH);
            Message next = mHandler.obtainMessage(ACTION_FLASH);
            mHandler.sendMessageDelayed(next, 500);
        } else {
            mHandler.removeMessages(ACTION_FLASH);
            mInputHintVisible = false;
            postInvalidate();
        }
    }

    private Rect mRect = new Rect();

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // 绘制外边框
        mRect.set(getBlockRect());
        canvas.drawRect(mRect, mBorderPaint);
        if (isFocus()) {
            if (mInputHintVisible) {
                canvas.drawLine(getContentRect().left + 10, getContentRect().top, getContentRect().left + 10, getContentRect().bottom, mInputHintPaint);
            }
        } else {
            canvas.drawRect(mRect, mBgPaint);
        }
    }
}
