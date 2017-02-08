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
 * Created by yangzc on 16/4/12.
 */
public class CYEditBlock extends CYPlaceHolderBlock implements CYEditable, CYEditableGroup {

    private static final int ACTION_FLASH = 1;

    protected Paint mBgPaint;
    protected Paint mBorderPaint;
    protected Paint mInputHintPaint;
    protected Paint mTextPaint;

    private boolean mInputHintVisible = false;
    private Handler mHandler;

    private int mHintPadding = 0;
    private String mText;

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

        mTextPaint = new Paint(getTextEnv().getPaint());

        mHintPadding = UIUtils.dip2px(2);
        Paint paint = getTextEnv().getPaint();
        int height = (int) (Math.ceil(paint.descent() - paint.ascent()) + 0.5f);
        setWidth(UIUtils.dip2px(80));
        setHeight(height);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMessageImpl(msg);
            }
        };
    }

    public CYEditBlock setHintPadding(int padding) {
        this.mHintPadding = padding;
        return this;
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
            CYPageView.FOCUS_TAB_ID = getTabId();
            mHandler.removeMessages(ACTION_FLASH);
            Message next = mHandler.obtainMessage(ACTION_FLASH);
            mHandler.sendMessageDelayed(next, 500);
        } else {
            mHandler.removeMessages(ACTION_FLASH);
            mInputHintVisible = false;
            postInvalidate();
        }
    }

    @Override
    public boolean hasFocus() {
        return CYPageView.FOCUS_TAB_ID == getTabId();
    }

    //    private Rect mRect = new Rect();

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect contentRect = getContentRect();
        Rect blockRect = getBlockRect();

        float textHintMarginLeft = 0;
        float textX = contentRect.left;
        if (!TextUtils.isEmpty(mText)) {
            textHintMarginLeft = mTextPaint.measureText(mText);
            if (textHintMarginLeft > contentRect.width()) {
                textX = contentRect.right - textHintMarginLeft;
                textHintMarginLeft = contentRect.width();
            } else {
                textX = contentRect.left + (contentRect.width() - textHintMarginLeft)/2;
            }
        }

        // 绘制外边框
        canvas.drawRect(blockRect, mBorderPaint);
        if (hasFocus()) {
            if (mInputHintVisible) {
                canvas.drawLine(contentRect.left + textHintMarginLeft, contentRect.top + mHintPadding,
                        contentRect.left + textHintMarginLeft, contentRect.bottom - mHintPadding, mInputHintPaint);
            }
        } else {
            canvas.drawRect(blockRect, mBgPaint);
        }

        if (!TextUtils.isEmpty(mText)) {
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            canvas.drawText(mText, textX, contentRect.bottom - fontMetrics.bottom, mTextPaint);
        }
    }

    @Override
    public int getTabId() {
        return 0;
    }

    @Override
    public void setTabId(int id) {
    }

    @Override
    public String getText() {
        return mText;
    }

    @Override
    public void setText(String text) {
        this.mText = text;
        postInvalidate();
//        requestLayout();
    }

    @Override
    public CYEditable findEditable(float x, float y) {
        return this;
    }

    @Override
    public CYEditable getFocusEditable() {
        if (hasFocus())
            return this;
        return null;
    }
}
