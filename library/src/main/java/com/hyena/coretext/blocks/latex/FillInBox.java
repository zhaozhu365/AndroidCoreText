package com.hyena.coretext.blocks.latex;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.hyena.coretext.CYPageView;
import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.IEditFace;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.EditableValue;
import com.hyena.framework.utils.UIUtils;

import maximsblog.blogspot.com.jlatexmath.core.Box;
import maximsblog.blogspot.com.jlatexmath.core.TeXFormula;
import maximsblog.blogspot.com.jlatexmath.core.Text;

/**
 * Created by yangzc on 17/6/27.
 */
public abstract class FillInBox extends Box implements ICYEditable {

    private static final int DP_1 = Const.DP_1;
    private static final int DP_2 = Const.DP_1 * 2;
    private static final int DP_3 = Const.DP_1 * 5;
    private static final int DP_56 = Const.DP_1 * 56;

    private TextEnv mTextEnv;
    private int mTabId;
    private Text mText;
    private IEditFace mEditFace;
    private float mScale = 1.0f;
    private Rect mBlockRect = new Rect();
    private Rect mContentRect = new Rect();
    private RectF mVisibleRect = new RectF();

    public FillInBox(TextEnv textEnv, int tabId, String clazz, Text text) {
        super();
        mEditFace = createEditFace();
        if (mEditFace == null) {
            throw new RuntimeException("createEditFace must be override!!!");
        }

        this.mTextEnv = textEnv;
        this.mTabId = tabId;
        this.mText = text;
        this.mScale = UIUtils.px2dip(textEnv.getContext(), textEnv.getPaint().getTextSize())
                * textEnv.getContext().getResources().getDisplayMetrics()
                .scaledDensity / TeXFormula.PIXELS_PER_POINT;

        setWidthWithScale(DP_56 + DP_3 + DP_3);
        setHeightWithScale(-mTextEnv.getPaint().ascent() + DP_1 + DP_2 + DP_2);
//        setDepth((mTextEnv.getPaint().descent() + DP_1) / mScale);
        setDepth(getHeight()/2);
        mTextEnv.setEditableValue(getTabId(), getText() == null ? mText.getText(): getText());
        mEditFace.setInEditMode(CYPageView.FOCUS_TAB_ID == getTabId());
    }

    /**
     * 设置动态宽度
     * @param width
     */
    public void setWidthWithScale(float width) {
        super.setWidth(width/mScale);
    }

    /**
     * 动态设置高度
     * @param height
     */
    public void setHeightWithScale(float height) {
        super.setHeight(height/mScale);
    }

    /**
     * 获取缩放比
     * @return
     */
    public float getScale() {
        return mScale;
    }

    /**
     * 获取编辑框皮肤
     * @return
     */
    public IEditFace getEditFace() {
        return mEditFace;
    }

    /**
     * 获得编辑框皮肤
     * @return 编辑框皮肤
     */
    public abstract IEditFace createEditFace();

    @Override
    public void draw(Canvas g2, float x, float y) {
        if (mEditFace == null)
            return;
        mVisibleRect.set(x, y - getHeight(), x + getWidth(), y);

        mBlockRect.set((int)(x * mScale + 0.5), (int)((y - getHeight()) * mScale + 0.5)
                , (int)((x + getWidth()) * mScale + 0.5), (int)((y + getDepth()) * mScale + 0.5));

        mContentRect.set(mBlockRect.left + DP_3, mBlockRect.top + DP_2,
                mBlockRect.right - DP_3, mBlockRect.bottom - DP_2);

        g2.save();
        float scale = mText.getMetrics().getSize() / mScale;
        g2.scale(scale,scale);
        mEditFace.onDraw(g2, mBlockRect, mContentRect);
        g2.restore();
    }

    @Override
    public int getLastFontId() {
        return mText.getTextFont().fontId;
    }

    @Override
    public void setFocus(boolean focus) {
        if (focus) {
            CYPageView.FOCUS_TAB_ID = getTabId();
        }
        if (mEditFace != null) {
            mEditFace.setInEditMode(focus);
        }
        mTextEnv.getEventDispatcher().postInvalidate(null);
    }

    @Override
    public boolean hasFocus() {
        return CYPageView.FOCUS_TAB_ID == getTabId();
    }

    @Override
    public void setFocusable(boolean focusable) {
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public int getTabId() {
        return mTabId;
    }

    @Override
    public String getText() {
        EditableValue value = mTextEnv.getEditableValue(getTabId());
        return value == null ? null : value.getValue();
    }

    @Override
    public void setText(String text) {
        mTextEnv.setEditableValue(getTabId(), text);
        mTextEnv.getEventDispatcher().requestLayout();
    }

    @Override
    public void setTextColor(int color) {
        EditableValue value = mTextEnv.getEditableValue(getTabId());
        if (value == null) {
            value = new EditableValue();
            mTextEnv.setEditableValue(getTabId(), value);
        }
        value.setColor(color);
        mTextEnv.getEventDispatcher().postInvalidate(null);
    }

    @Override
    public void setEditable(boolean editable) {
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public String getDefaultText() {
        return null;
    }

    /**
     * 获得可见区域
     * @return
     */
    public RectF getVisibleRect() {
        return mVisibleRect;
    }

    /**
     * 释放
     */
    public void release() {
        if (mEditFace != null)
            mEditFace.release();
    }

}
