package com.hyena.coretext;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.ICYEditableGroup;
import com.hyena.coretext.event.CYEventDispatcher;
import com.hyena.coretext.event.CYFocusEventListener;
import com.hyena.coretext.event.CYLayoutEventListener;
import com.hyena.coretext.utils.CYBlockUtils;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYPageView extends View implements CYLayoutEventListener {

    public static int FOCUS_TAB_ID = -1;
    private CYPageBlock mPageBlock;
    private CYBlock mFocusBlock;
    private ICYEditable mFocusEditable;

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
    public ICYEditable findEditableByTabId(int tabId) {
        if (mPageBlock != null) {
            return mPageBlock.findEditableByTabId(tabId);
        }
        return null;
    }

    public void setText(int tabId, String text) {
        ICYEditable editable = findEditableByTabId(tabId);
        if (editable != null) {
            editable.setText(text);
        }

    }

    public String getText(int tabId) {
        ICYEditable editable = findEditableByTabId(tabId);
        if (editable != null) {
            return editable.getText();
        }
        return null;
    }

    public void setFocus(int tabId) {
        ICYEditable editable = findEditableByTabId(tabId);
        if (editable != null) {
            editable.setFocus(true);
            if (mFocusEditable != null) {
                mFocusEditable.setFocus(false);
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
                onTouchDown(event);

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

    private void onTouchDown(MotionEvent event) {
        handleFocusEvent(event);
    }

    /**
     * handle Focus Event
     * @param event motionEvent
     */
    private void handleFocusEvent(MotionEvent event) {
        int x = (int) event.getX() - mPageBlock.getPaddingLeft();
        int y = (int) event.getY() - mPageBlock.getPaddingTop();

        CYBlock focusBlock = CYBlockUtils.findBlockByPosition(mPageBlock, x, y);
        this.mFocusBlock = focusBlock;
        if (focusBlock != null && focusBlock.isFocusable() && focusBlock != mFocusEditable) {
            //make last focus item to false
            if (mFocusEditable != null) {
                mFocusEditable.setFocus(false);
                if (mFocusEditable instanceof ICYEditable) {
                    notifyFocusChange(false, mFocusEditable);
                } else if (mFocusEditable instanceof ICYEditableGroup) {
                    ICYEditable editable = ((ICYEditableGroup) mFocusEditable).getFocusEditable();
                    if (editable != null)
                        notifyFocusChange(false, editable);
                }
            }

            //make current focus active
            if (focusBlock instanceof ICYEditable) {
                FOCUS_TAB_ID = ((ICYEditable) focusBlock).getTabId();
                notifyFocusChange(true, (ICYEditable) focusBlock);
            } else if (focusBlock instanceof ICYEditableGroup) {
                ICYEditable editable = ((ICYEditableGroup) focusBlock).findEditable(x - focusBlock.getX(),
                        y - focusBlock.getLineY());
                if (editable != null) {
                    FOCUS_TAB_ID = editable.getTabId();
                    notifyFocusChange(true, editable);
                }
            }
        }
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
        if (mFocusEditable != null) {
            mFocusEditable.setFocus(false);
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

    private void notifyFocusChange(boolean hasFocus, ICYEditable editable) {
        if (hasFocus) {
            this.mFocusEditable = editable;
        }
        editable.setFocus(hasFocus);
        if (mFocusEventListener != null) {
            mFocusEventListener.onFocusChange(hasFocus, editable.getTabId());
        }
    }
}
