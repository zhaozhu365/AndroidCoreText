package com.hyena.coretext.blocks;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.event.CYEventDispatcher;
import com.hyena.framework.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 16/4/8.
 */
public abstract class CYBlock<T extends CYBlock> implements ICYFocusable {

    private static final String TAG = "CYBlock";
    //当前块横坐标
    private int x;
    //当前行上边界
    private int lineY;
    //当前行高度
    private int lineHeight;
    private int paddingLeft = 0, paddingTop = 0, paddingRight = 0, paddingBottom = 0;
    //是否存在焦点
    private boolean mFocus = false;
    //内容范围
    private Rect mContentRect = new Rect();
    private Rect mBlockRect = new Rect();
    //所有子节点
    private List<T> mChildren = new ArrayList<T>();
    private TextEnv mTextEnv;

    private Paint mPaint;
    //是否在独享行中
    private boolean mIsInMonopolyRow = true;
    private boolean mFocusable = false;

    public CYBlock(TextEnv textEnv, String content) {
        this.mTextEnv = textEnv;
        this.paddingTop = UIUtils.dip2px(2);
        this.paddingBottom = UIUtils.dip2px(2);
        if (isDebug()) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.STROKE);
        }
    }

    public TextEnv getTextEnv() {
        return mTextEnv;
    }

    /**
     * @param x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * @param lineY top y
     */
    public void setLineY(int lineY) {
        this.lineY = lineY;
    }

    /**
     * @return current line top y
     */
    public int getLineY() {
        return lineY;
    }

    /**
     * @param lineHeight current line height
     */
    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    /**
     * 获得前行高度
     * @return 高度
     */
    public int getLineHeight() {
        return lineHeight;
    }

    /*
     * set padding
     */
    public void setPadding(int left, int top, int right, int bottom) {
        this.paddingLeft = left;
        this.paddingTop = top;
        this.paddingRight = right;
        this.paddingBottom = bottom;
    }

    /**
     * @return padding left
     */
    public int getPaddingLeft() {
        return paddingLeft;
    }

    /**
     * @return padding top
     */
    public int getPaddingTop() {
        return paddingTop;
    }

    /**
     * @return padding right
     */
    public int getPaddingRight() {
        return paddingRight;
    }

    /**
     * @return padding bottom
     */
    public int getPaddingBottom() {
        return paddingBottom;
    }

    /**
     * @return width of content
     */
    public abstract int getContentWidth();

    /**
     * @return height of content
     */
    public abstract int getContentHeight();

    public int getHeight() {
        return getBlockRect().height();
    }

    public int getWidth() {
        return getBlockRect().width();
    }

    /**
     * set is in monopoly row
     * @param isInMonopolyRow isInMonopolyRow
     */
    public void setIsInMonopolyRow(boolean isInMonopolyRow) {
        this.mIsInMonopolyRow = isInMonopolyRow;
    }

    /**
     * draw block
     * @param canvas canvas
     */
    public void draw(Canvas canvas) {
        if (isDebug()) {
            canvas.drawRect(getContentRect(), mPaint);
            canvas.drawRect(getBlockRect(), mPaint);
        }
    }

    /**
     * add child
     * @param child child block
     */
    public void addChild(T child) {
        if (mChildren == null)
            mChildren = new ArrayList<T>();
        mChildren.add(child);
    }

    /**
     * @return child of block
     */
    public List<T> getChildren() {
        return mChildren;
    }

    /**
     * @return rect of content
     */
    public Rect getContentRect() {
        int left = x + paddingLeft;
        int right = x + paddingLeft + getContentWidth();

        TextEnv.Align align = getTextEnv().getTextAlign();
        int contentHeight = getContentHeight();
        int top;
        if (align == TextEnv.Align.TOP || mIsInMonopolyRow) {
            top = lineY + paddingTop;
        } else if (align == TextEnv.Align.CENTER) {
            top = lineY + (getLineHeight() - contentHeight)/2;
        } else {
            top = lineY + getLineHeight() - contentHeight - paddingBottom;
        }
        mContentRect.set(left, top, right, top + contentHeight);
        return mContentRect;
    }

    /**
     * @return rect of block
     */
    public Rect getBlockRect() {
        int left = x;
        int right = x + getContentWidth() + paddingLeft + paddingRight;

        TextEnv.Align align = getTextEnv().getTextAlign();
        int contentHeight = getContentHeight();
        int top;
        if (align == TextEnv.Align.TOP || mIsInMonopolyRow) {
            top = lineY;
        } else if(align == TextEnv.Align.CENTER) {
            top = lineY + (getLineHeight() - contentHeight)/2 - paddingTop;
        } else {
            top = lineY + getLineHeight() - contentHeight - paddingTop - paddingBottom;
        }
        mBlockRect.set(left, top, right, top + contentHeight + paddingTop + paddingBottom);
        return mBlockRect;
    }

    public boolean onTouchEvent(int action, float x, float y) {
        if (isDebug())
            debug("onEvent: " + action);
        return false;
    }

    /**
     * relayout
     */
    public void requestLayout() {
        CYEventDispatcher.getEventDispatcher().requestLayout();
    }

    /**
     * force relayout
     * @param force force or not
     */
    public void requestLayout(boolean force) {
        CYEventDispatcher.getEventDispatcher().requestLayout(force);
    }

    /**
     * reDraw
     */
    public void postInvalidate() {
        CYEventDispatcher.getEventDispatcher().postInvalidate();
    }

    public boolean isDebug() {
        return false;
    }

    protected void debug(String msg) {
        Log.v(TAG, msg);
    }


    /**
     * @param focus mark force or not
     */
    @Override
    public void setFocus(boolean focus) {
        if (isFocusable()) {
            mFocus = focus;
            if (isDebug())
                debug("rect: " + getBlockRect().toString() + ", focus: " + focus);
        }
    }

    /**
     * @return force or not
     */
    @Override
    public boolean hasFocus(){
        return mFocus;
    }

    @Override
    public void setFocusable(boolean focusable) {
        this.mFocusable = focusable;
    }

    @Override
    public boolean isFocusable() {
        return mFocusable;
    }

    /**
     * find editable by tabId
     * @param tabId tabId
     * @return
     */
    public ICYEditable findEditableByTabId(int tabId) {
        List<T> children = getChildren();
        if (children != null && !children.isEmpty()) {
            for (int i = 0; i < children.size(); i++) {
                T block = children.get(i);
                ICYEditable editable = block.findEditableByTabId(tabId);
                if (editable != null) {
                    return editable;
                }
            }
        } else {
            if (this instanceof CYEditBlock && ((CYEditBlock)this).getTabId() == tabId) {
                return (ICYEditable) this;
            }
        }
        return null;
    }

    public void release() {
        List<T> children = getChildren();
        if (children != null && !children.isEmpty()) {
            for (int i = 0; i < children.size(); i++) {
                T block = children.get(i);
                block.release();
            }
        }
    }
}
