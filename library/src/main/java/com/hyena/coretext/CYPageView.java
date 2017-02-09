package com.hyena.coretext;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYEditable;
import com.hyena.coretext.blocks.CYEditableGroup;
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

    public static int FOCUS_TAB_ID = -1;
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

    public void setText(int tabId, String text) {
        CYEditable editable = findEditableByTabId(tabId);
        if (editable != null) {
            editable.setText(text);
        }

    }

    public String getText(int tabId) {
        CYEditable editable = findEditableByTabId(tabId);
        if (editable != null) {
            return editable.getText();
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
        if (mPageBlock == null)
            return super.onTouchEvent(event);

        int action = MotionEventCompat.getActionMasked(event);
        int x = (int) event.getX() - mPageBlock.getPaddingLeft();
        int y = (int) event.getY() - mPageBlock.getPaddingTop();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                CYBlock focusBlock = CYBlockUtils.findBlockByPosition(mPageBlock, x, y);
                if (focusBlock == null || focusBlock != mFocusBlock) {
                    if (mFocusBlock != null) {
                        mFocusBlock.setFocus(false);

                        if (mFocusEventListener != null && mFocusBlock instanceof CYEditableGroup) {
                            mFocusEventListener.onFocusChange(false, ((CYEditableGroup) mFocusBlock).getFocusEditable().getTabId());
                        }
                    }
                }

                mFocusBlock = focusBlock;
                if (mFocusBlock != null) {
                    mFocusBlock.setFocus(true);
                    if (mFocusBlock instanceof CYEditableGroup) {
                        CYEditable editable = ((CYEditableGroup) mFocusBlock).findEditable(x - mFocusBlock.getX(),
                                y - mFocusBlock.getLineY());
                        if (editable != null) {
                            editable.setFocus(true);
                            if (mFocusEventListener != null) {
                                mFocusEventListener.onFocusChange(true, editable.getTabId());
                            }
                        }
                    }
                }

                if (mFocusBlock != null) {
                    mFocusBlock.onTouchEvent(action, x - mFocusBlock.getX(),
                            y - mFocusBlock.getLineY());
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE: {
                if (mFocusBlock != null) {
                    mFocusBlock.onTouchEvent(action, x - mFocusBlock.getX(),
                            y - mFocusBlock.getLineY());
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
