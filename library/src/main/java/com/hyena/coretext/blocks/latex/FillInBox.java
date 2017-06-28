package com.hyena.coretext.blocks.latex;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditFace;
import com.hyena.coretext.blocks.ICYEditable;
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
    private static final int DP_3 = Const.DP_1 * 3;
    private static final int DP_56 = Const.DP_1 * 56;

    private TextEnv mTextEnv;
    private int mTabId;
    private Text mText;
    private CYEditFace mEditFace;
    private float mScale = 1.0f;
    private Rect mBlockRect = new Rect();
    private RectF mVisibleRect = new RectF();

    public FillInBox(TextEnv textEnv, int tabId, String clazz, Text text) {
        super();
        mEditFace = getEditFace();
        if (mEditFace == null)
            return;

        this.mTextEnv = textEnv;
        this.mTabId = tabId;
        this.mText = text;
        this.mScale = UIUtils.px2dip(textEnv.getContext(), textEnv.getPaint().getTextSize())
                * textEnv.getContext().getResources().getDisplayMetrics()
                .scaledDensity / TeXFormula.PIXELS_PER_POINT;

        setWidth(DP_56/ mScale);
        setHeight((-mEditFace.getTextPaint().ascent() + DP_1) / mScale);
        setDepth((mEditFace.getTextPaint().descent() + DP_1) / mScale);
//            mEditFace.setPadding(DP_3, DP_2, DP_3, DP_2);
        setText(text.getText());
    }

    @Override
    public void setWidth(float w) {
        super.setWidth(w);
    }

    /**
     * 获得编辑框皮肤
     * @return 编辑框皮肤
     */
    public abstract CYEditFace getEditFace();

    @Override
    public void draw(Canvas g2, float x, float y) {
        if (mEditFace == null)
            return;

        mVisibleRect.set(x, y - getHeight(), x + getWidth(), y);
        mBlockRect.set((int)(x * mScale + 0.5), (int)((y - getHeight()) * mScale + 0.5)
                , (int)((x + getWidth()) * mScale + 0.5), (int)((y + getDepth()) * mScale + 0.5));
        g2.save();
        g2.scale(mText.getMetrics().getSize() / mScale, mText.getMetrics().getSize() / mScale);
        mEditFace.onDraw(g2, mBlockRect, mBlockRect);
        g2.restore();
    }

    @Override
    public int getLastFontId() {
        return mText.getTextFont().fontId;
    }

    @Override
    public void setFocus(boolean focus) {
        if (mEditFace != null)
            mEditFace.setFocus(focus);
    }

    @Override
    public boolean hasFocus() {
        if (mEditFace != null)
            return mEditFace.hasFocus();
        return false;
    }

    @Override
    public void setFocusable(boolean focusable) {
        if (mEditFace != null)
            mEditFace.setEditable(focusable);
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
    }

    @Override
    public void setTextColor(int color) {
        EditableValue value = mTextEnv.getEditableValue(getTabId());
        if (value == null) {
            value = new EditableValue();
            mTextEnv.setEditableValue(getTabId(), value);
        }
        value.setColor(color);
        mEditFace.setTextColor(color);
        mTextEnv.getEventDispatcher().postInvalidate(null);
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
