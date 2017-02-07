package com.hyena.coretext;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYEditable;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.event.CYEventDispatcher;
import com.hyena.coretext.event.CYFocusEventListener;
import com.hyena.coretext.event.CYLayoutEventListener;
import com.hyena.coretext.utils.CYBlockUtils;
import com.hyena.framework.clientlog.LogUtil;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYPageView extends View implements CYLayoutEventListener {

    private CYPageBlock mPageBlock;
    private CYBlock mFocusBlock;

    public CYPageView(Context context) {
        super(context);
    }

    public CYPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CYPageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPageBlock != null) {
            canvas.save();
            canvas.translate(mPageBlock.getPaddingLeft(), mPageBlock.getPaddingTop());
            mPageBlock.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec)
                , getMeasureHeight(heightMeasureSpec));
    }

    private int getMeasureHeight(int heightSpec) {
        int mode = MeasureSpec.getMode(heightSpec);
        int size = MeasureSpec.getSize(heightSpec);
        switch (mode) {
            case MeasureSpec.EXACTLY: {
                return size;
            }
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST: {
                if (mPageBlock != null)
                    return mPageBlock.getHeight();
            }
        }
        return size;
    }

    /**
     * set blocks items
     * @param pageBlock page
     */
    public void setPageBlock(CYPageBlock pageBlock) {
        this.mPageBlock = pageBlock;
        postInvalidate();
    }

    /**
     * find editable by tabId
     * @param tabId tabId
     * @return
     */
    public CYEditable findEditableByTabId(int tabId) {
        if (mPageBlock != null) {
            return mPageBlock.findEditableByTabId(tabId);
        }
        return null;
    }

    public void setFocus(int tabId) {
        CYEditable editable = findEditableByTabId(tabId);
        if (editable != null) {
            editable.setFocus(true);
            if (mFocusBlock != null) {
                mFocusBlock.setFocus(false);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        LogUtil.v("yangzc", "event action -->" + event.getAction());
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                CYBlock focusBlock = CYBlockUtils.findBlockByPosition(mPageBlock,
                        (int) event.getX(), (int) event.getY());
                if (focusBlock == null || focusBlock != mFocusBlock) {
                    if (mFocusBlock != null) {
                        mFocusBlock.setFocus(false);
                        if (mFocusBlock instanceof CYEditable && mFocusEventListener != null) {
                            mFocusEventListener.onFocusChange(false, (CYEditable) mFocusBlock);
                        }
                    }
                }

                mFocusBlock = focusBlock;
                if (mFocusBlock != null) {
                    mFocusBlock.setFocus(true);
                    if (mFocusBlock instanceof CYEditable && mFocusEventListener != null) {
                        mFocusEventListener.onFocusChange(true, (CYEditable) mFocusBlock);
                    }
                }

                if (mFocusBlock != null) {
                    mFocusBlock.onTouchEvent(action, event.getX() - mFocusBlock.getX(),
                            event.getY() - mFocusBlock.getLineY());
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE: {
                if (mFocusBlock != null) {
                    mFocusBlock.onTouchEvent(action, event.getX() - mFocusBlock.getX(),
                            event.getY() - mFocusBlock.getLineY());
                }
                break;
            }
        }
        return true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        CYEventDispatcher.getEventDispatcher().addLayoutEventListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        CYEventDispatcher.getEventDispatcher().removeLayoutEventListener(this);
        if (mFocusBlock != null) {
            mFocusBlock.setFocus(false);
        }
        if (mPageBlock != null) {
            mPageBlock.release();
        }
    }

    @Override
    public void onLayout(int pageWidth, int pageHeight) {
        requestLayout();
    }

    @Override
    public void onInvalidate() {
        postInvalidate();
    }

    private CYFocusEventListener mFocusEventListener = null;

    public void setFocusEventListener(CYFocusEventListener listener) {
        this.mFocusEventListener = listener;
    }
}
